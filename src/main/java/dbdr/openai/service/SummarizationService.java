package dbdr.openai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dbdr.domain.chart.service.ChartService;
import dbdr.global.configuration.OpenAiSummarizationConfig;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.openai.dto.request.ChartDataRequest;
import dbdr.openai.dto.request.OpenAiSummaryRequest;
import dbdr.openai.dto.etc.Message;
import dbdr.openai.dto.response.OpenAiSummaryResponse;
import dbdr.openai.dto.response.SummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummarizationService {

    private final OpenAiSummarizationConfig summarizationConfig;
    private final ChartService chartService;

    @Value("${openai.chat-completions}")
    private String chatUrl;

    @Value("${openai.model}")
    private String model;

    public SummaryResponse getTextAndGetSummary(Long recipientId) {
        HttpHeaders headers = summarizationConfig.httpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";

        ChartDataRequest text = chartService.getSevenDaysChart(recipientId);

        try {
            jsonString = objectMapper.writeValueAsString(text);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.JSON_PARSING_ERROR);
        }

        Message userMessage = new Message("user", jsonString);
        List<Message> messageList = List.of(userMessage);
        OpenAiSummaryRequest request = new OpenAiSummaryRequest(model, messageList);
        ResponseEntity<OpenAiSummaryResponse> response = summarizationConfig.restTemplate()
            .exchange(chatUrl, HttpMethod.POST, new HttpEntity<>(request, headers),
                OpenAiSummaryResponse.class);

        try {
            return objectMapper.readValue(response.getBody().choices().get(0).message().content(),
                SummaryResponse.class);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.JSON_PARSING_ERROR);
        }
    }
}
