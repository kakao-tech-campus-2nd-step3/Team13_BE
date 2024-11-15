package dbdr.openAi;

import dbdr.domain.chart.service.ChartService;
import dbdr.global.configuration.OpenAiSummarizationConfig;
import dbdr.openai.dto.etc.Choice;
import dbdr.openai.dto.etc.CompletionTokensDetails;
import dbdr.openai.dto.etc.Message;
import dbdr.openai.dto.etc.Usage;
import dbdr.openai.dto.response.OpenAiSummaryResponse;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class OpenAiTest {

    @InjectMocks
    private ChartService chartService;

    @Mock
    private OpenAiSummarizationConfig summarizationConfig;

    @Mock
    private RestTemplate restTemplate;

    @Value("${openai.chat-completions}")
    private String chatUrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(summarizationConfig.restTemplate()).thenReturn(restTemplate);
    }

    @Test
    void testOpenAiResponse_SuccessfulResponse() {
        String testInput = "Sample text";
        String tempModel = "test-model";
        String expectedContent = "Test summary response";

        HttpHeaders headers = new HttpHeaders();
        when(summarizationConfig.httpHeaders()).thenReturn(headers);

        Message message = new Message("user", expectedContent);
        Choice choice = new Choice(0, message, null, "stop");

        CompletionTokensDetails completionTokensDetails = new CompletionTokensDetails(5);
        Usage usage = new Usage(10, 10, 20, completionTokensDetails);
        OpenAiSummaryResponse mockResponse = new OpenAiSummaryResponse(
            "id", "object", System.currentTimeMillis(), "model", "fingerprint", List.of(choice), usage
        );

        ResponseEntity<OpenAiSummaryResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(chatUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(OpenAiSummaryResponse.class)
        )).thenReturn(responseEntity);

        OpenAiSummaryResponse result = chartService.openAiResponse(testInput, tempModel);

        assertNotNull(result);
        assertEquals(expectedContent, result.choices().get(0).message().content());
        assertEquals("user", result.choices().get(0).message().role());
        assertEquals("stop", result.choices().get(0).finishReason());
    }

    @Test
    void testOpenAiResponse_FailureAndRetry() {
        String testInput = "Sample text for OpenAI";
        String tempModel = "test-model";

        // Mock headers
        HttpHeaders headers = new HttpHeaders();
        when(summarizationConfig.httpHeaders()).thenReturn(headers);

        // Simulate failure response
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(OpenAiSummaryResponse.class)
        )).thenReturn(
            new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR),
            new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR),
            new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR)
        );

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
            chartService.openAiResponse(testInput, tempModel)
        );

        assertEquals(ApplicationError.OPEN_AI_ERROR, exception.getApplicationError());
    }
}
