package dbdr.openai.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record ChartDataRequest(
    String conditionDisease,
    String bodyManagement,
    String nursingManagement,
    String cognitiveManagement,
    String recoveryTraining) {

}
