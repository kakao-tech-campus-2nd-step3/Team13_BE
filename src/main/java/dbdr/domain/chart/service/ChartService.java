package dbdr.domain.chart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.openai.dto.request.ChartDataRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ChartRepository chartRepository;
    private final ChartMapper chartMapper;

    public Page<ChartDetailResponse> getAllChartByRecipientId(Long recipientId, Pageable pageable) {
        Page<Chart> results = chartRepository.findAllByRecipientId(recipientId, pageable);
        return results.map(chartMapper::toResponse);
    }

    public ChartDetailResponse getChartById(Long chartId) {
        Chart chart = chartRepository.findById(chartId).orElseThrow(); // 에러 처리 필요
        return chartMapper.toResponse(chart);
    }

    public void deleteChart(Long chartId) {
        chartRepository.deleteById(chartId);
    }

    public ChartDetailResponse saveChart(ChartDetailRequest request) {
        Chart chart = chartMapper.toEntity(request);
        Chart savedChart = chartRepository.save(chart);
        return chartMapper.toResponse(savedChart);
    }

    public ChartDetailResponse updateChart(Long chartId, ChartDetailRequest request) {
        Chart chart = chartRepository.findById(chartId).orElseThrow(); // 에러 처리 필요
        chart.update(chartMapper.toEntity(request));
        Chart savedChart = chartRepository.save(chart);
        return chartMapper.toResponse(savedChart);
    }

    public ChartDataRequest getSevenDaysChart(Long recipientId) {
        List<ChartDetailResponse> chartList = getChartsFromLastSevenDays(recipientId);

        StringBuilder conditionDisease = new StringBuilder();

        String bodyManagement = formatSection("신체활동", chartList,
            ChartDetailResponse::bodyManagement);
        conditionDisease.append("상태: ").append(collectConditionDisease(chartList));
        String nursingManagement = formatSection("간호관리", chartList,
            ChartDetailResponse::nursingManagement);
        String recoveryTraining = formatSection("기능회복훈련", chartList,
            ChartDetailResponse::recoveryTraining);
        String cognitiveManagement = formatSection("인지관리", chartList,
            ChartDetailResponse::cognitiveManagement);

        return new ChartDataRequest(cognitiveManagement, bodyManagement,
            recoveryTraining, conditionDisease.toString(), nursingManagement);
    }

    private List<ChartDetailResponse> getChartsFromLastSevenDays(Long recipientId) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = currentDate.minusDays(7);
        List<Chart> chartList = chartRepository.findAllWithinSevenDaysByRecipientId(recipientId,sevenDaysAgo, currentDate);
        return chartList.stream().map(chartMapper::toResponse).toList();
    }

    private <T> String formatSection(String sectionName, List<ChartDetailResponse> chartList,
        Function<ChartDetailResponse, T> mapper) {
        return chartList.stream()
            .map(mapper)
            .filter(Objects::nonNull)
            .map(this::convertToReadableString)
            .collect(Collectors.joining("; ", sectionName + ": ", ""));
    }

    private String convertToReadableString(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<>() {});

            String createdAt = (String) map.getOrDefault("createdAt", "알 수 없음");
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
        return key + " " + formattedValue;
    }

    private String formatDateLabel(String createdAt) {
        try {
            if (createdAt.length() >= 10) {
                LocalDate date = LocalDate.parse(createdAt.substring(0, 10)); // Extract "YYYY-MM-DD"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
                return date.format(formatter);
            }
            else return null;
        } catch (DateTimeParseException e) {
            throw new ApplicationException(ApplicationError.CANNOT_DETECT_DATE);
        }
    }

    private String collectConditionDisease(List<ChartDetailResponse> chartList) {
        return chartList.stream()
            .map(ChartDetailResponse::conditionDisease)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
    }
}
