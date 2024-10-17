package dbdr.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SummaryResponse(
    @JsonProperty("인지관리") String cognitiveManagement,
    @JsonProperty("신체활동") String bodyManagement,
    @JsonProperty("기능회복훈련") String recoveryTraining,
    @JsonProperty("상태") String condition,
    @JsonProperty("간호관리") String nursingManagement) {

}
