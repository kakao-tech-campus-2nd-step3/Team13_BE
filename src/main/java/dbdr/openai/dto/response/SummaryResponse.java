package dbdr.openai.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record SummaryResponse(
    String cognitiveManagement,
    String bodyManagement,
    String recoveryTraining,
    String conditionDisease,
    String nursingManagement) {

}
