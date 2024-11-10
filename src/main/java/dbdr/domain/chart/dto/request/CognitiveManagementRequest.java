package dbdr.domain.chart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CognitiveManagementRequest(
        @Schema(description = "의사소통 도움 여부", example = "true")
        boolean cognitiveHelp, // 의사소통 도움 여부
        @Schema(description = "말벗 및 격려 여부", example = "true")
        boolean companionshipProvided,
        @Schema(description = "인지 관리 특이사항", example = "없음")
        String cognitiveNote // 인지 관리 특이사항
) {
}
