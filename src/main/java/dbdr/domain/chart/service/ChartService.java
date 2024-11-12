package dbdr.domain.chart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.ChartOverviewResponse;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.List;
import java.util.stream.Collectors;
import dbdr.global.configuration.OpenAiSummarizationConfig;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.openai.dto.etc.Message;
import dbdr.openai.dto.request.ChartDataRequest;
import dbdr.openai.dto.request.OpenAiSummaryRequest;
import dbdr.openai.dto.response.OpenAiSummaryResponse;
import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.dto.response.TagResponse;
import dbdr.openai.entity.Summary;
import dbdr.openai.repository.SummaryRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final ChartRepository chartRepository;
    private final ChartMapper chartMapper;
    private final SummaryRepository summaryRepository;
    private final OpenAiSummarizationConfig summarizationConfig;
    private final AlarmService alarmService;

    @Value("${openai.chat-completions}")
    private String chatUrl;

    @Value("${openai.model}")
    private String modelOne;

    @Value("${openai.model-tag}")
    private String modelTwo;

    public List<ChartOverviewResponse> getAllChartByRecipientId(Long recipientId) {
        List<Chart> results = chartRepository.findAllByRecipientId(recipientId);
        return results.stream()
                .map(chartMapper::toOverviewResponse)
                .collect(Collectors.toList());
    }

    public ChartDetailResponse getChartById(Long chartId) {
        Chart chart = chartRepository.findById(chartId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CHART_NOT_FOUND));
        return chartMapper.toResponse(chart);
    }

    public void deleteChart(Long chartId) {
        chartRepository.deleteById(chartId);
    }

    @Transactional
    public ChartDetailResponse saveChart(ChartDetailRequest request) {
        Chart chart = chartMapper.toEntity(request);
        Chart savedChart = chartRepository.save(chart);
        SummaryResponse summaryResponse = getTextAndGetSummary(savedChart);
        TagResponse tagResponse = getTag(summaryResponse);
        summaryRepository.save(
            new Summary(savedChart.getId(), summaryResponse.cognitiveManagement(),
                summaryResponse.bodyManagement(), summaryResponse.recoveryTraining(),
                summaryResponse.conditionDisease(), summaryResponse.nursingManagement(),
                tagResponse.tag1(), tagResponse.tag2(), tagResponse.tag3()));
        ChartDetailResponse chartDetailResponse = chartMapper.toResponse(savedChart);
        alarmService.updateGuardianAlarmMessage(chartDetailResponse);
        return chartDetailResponse;
    }

    public ChartDetailResponse updateChart(Long chartId, ChartDetailRequest request) {
        Chart chart = chartRepository.findById(chartId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CHART_NOT_FOUND));
        chart.update(chartMapper.toEntity(request));
        Chart savedChart = chartRepository.save(chart);
        Summary summary = summaryRepository.findByChartId(chartId);
        SummaryResponse summaryResponse = getTextAndGetSummary(savedChart);
        TagResponse tagResponse = getTag(summaryResponse);
        summary.update(summaryResponse.cognitiveManagement(), summaryResponse.bodyManagement(),
            summaryResponse.recoveryTraining(), summaryResponse.conditionDisease(),
            summaryResponse.nursingManagement(), tagResponse.tag1(), tagResponse.tag2(), tagResponse.tag3());
        summaryRepository.save(summary);
        return chartMapper.toResponse(savedChart);
    }

    private TagResponse getTag(SummaryResponse summaryResponse) {
        String str = String.format(
            "%s, %s, %s, %s, %s",
            summaryResponse.cognitiveManagement(), summaryResponse.bodyManagement(),
            summaryResponse.recoveryTraining(), summaryResponse.conditionDisease(),
            summaryResponse.nursingManagement()
        );
        OpenAiSummaryResponse response = openAiResponse(str, modelTwo);
        return parseTagString(response.choices().get(0).message().content());
    }

    private TagResponse parseTagString(String tagString) {
        String[] tags = tagString.split(", ");
        String tag1 = tags[0].split(": ")[1].trim();
        String tag2 = tags[1].split(": ")[1].trim();
        String tag3 = tags[2].split(": ")[1].trim();

        return new TagResponse(tag1, tag2, tag3);
    }

    public OpenAiSummaryResponse openAiResponse(String str, String tempModel) {
        HttpHeaders headers = summarizationConfig.httpHeaders();
        Message userMessage = new Message("user", str);
        List<Message> messageList = List.of(userMessage);
        OpenAiSummaryRequest request = new OpenAiSummaryRequest(tempModel, messageList);
        ResponseEntity<OpenAiSummaryResponse> response = summarizationConfig.restTemplate()
            .exchange(chatUrl, HttpMethod.POST, new HttpEntity<>(request, headers),
                OpenAiSummaryResponse.class);
        return response.getBody();
    }

    private SummaryResponse getTextAndGetSummary(Chart chart) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";

        ChartDataRequest text = getSelectedDatesSummarization(chart);

        try {
            jsonString = objectMapper.writeValueAsString(text);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.JSON_PARSING_ERROR);
        }

        OpenAiSummaryResponse response = openAiResponse(jsonString, modelOne);

        log.debug("API Response: " + response);

        try {
            return objectMapper.readValue(response.choices().get(0).message().content(),
                SummaryResponse.class);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.JSON_PARSING_ERROR);
        }
    }

    private ChartDataRequest getSelectedDatesSummarization(Chart chart) {
        ChartDetailResponse chartDetailResponse = chartMapper.toResponse(chart);

        String conditionDisease = chartDetailResponse.conditionDisease();

        String bodyManagement = formatSection(chartDetailResponse,
            ChartDetailResponse::bodyManagement);

        String nursingManagement = formatSection(chartDetailResponse,
            ChartDetailResponse::nursingManagement);

        String cognitiveManagement = formatSection(chartDetailResponse,
            ChartDetailResponse::cognitiveManagement);

        String recoveryTraining = formatSection(chartDetailResponse,
            ChartDetailResponse::recoveryTraining);

        return new ChartDataRequest(conditionDisease, bodyManagement, nursingManagement,
            cognitiveManagement, recoveryTraining);
    }

    private <T> String formatSection(ChartDetailResponse chartDetailResponse,
        Function<ChartDetailResponse, T> mapper) {
        T sectionData = mapper.apply(chartDetailResponse);
        if (sectionData != null) {
            return convertToReadableString(sectionData);
        }
        return "";
    }

    private String convertToReadableString(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<>() {
            });

            String createdAt = (String) map.getOrDefault("createdAt", "unknown");
            String dateLabel = formatDateLabel(createdAt);

            return map.entrySet().stream()
                .map(entry -> formatEntry(entry.getKey(), entry.getValue(), dateLabel))
                .collect(Collectors.joining(", "));
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ApplicationError.JSON_PARSING_ERROR);
        }
    }

    private String formatEntry(String key, Object value, String dateLabel) {
        if ("id".equals(key)) {
            return dateLabel;
        }
        String formattedValue = (value != null) ? value.toString() : "없음";
        return key + ": " + formattedValue;
    }

    private String formatDateLabel(String createdAt) {
        try {
            if (createdAt.length() >= 10) {
                LocalDate date = LocalDate.parse(
                    createdAt.substring(0, 10));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
                return date.format(formatter);
            } else {
                return null;
            }
        } catch (DateTimeParseException e) {
            throw new ApplicationException(ApplicationError.CANNOT_DETECT_DATE);
        }
    }
}
