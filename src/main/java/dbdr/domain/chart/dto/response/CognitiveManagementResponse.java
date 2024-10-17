package dbdr.domain.chart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CognitiveManagementResponse(
    Long id,
    @JsonProperty("의사소통 도움 여부") boolean cognitiveHelp, // 의사소통 도움 여부
    @JsonProperty("인지관리 특이 사항") String cognitiveNote // 인지 관리 특이사항
) {
}
