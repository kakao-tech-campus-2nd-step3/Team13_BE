package dbdr.openai.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record SummaryResponse(
    String conditionDisease,
    String bodyManagement,
    String nursingManagement,
    String cognitiveManagement,
    String recoveryTraining) {

}
