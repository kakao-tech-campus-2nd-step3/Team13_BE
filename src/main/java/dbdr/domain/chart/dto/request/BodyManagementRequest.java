package dbdr.domain.chart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BodyManagementRequest(
        @Schema(description = "세면 유무", example = "true")
        boolean wash, // 세면 유무
        @Schema(description = "목욕 유무", example = "true")
        boolean bath, // 목욕 유무
        @Schema(description = "식사 종류", example = "채식")
        String mealType, // 식사 종류
        @Schema(description = "섭취량", example = "3회")
        String intakeAmount, // 섭취량
        @Schema(description = "화장실 횟수", example = "6")
        String physicalRestroom, // 화장실 횟수
        @Schema(description = "산책", example = "false")
        boolean hasWalked, // 산책
        @Schema(description = "체위 변경 유무", example = "false")
        boolean positionChangeRequired, // 체위 변경 유무
        @Schema(description = "이동 도움 유무", example = "false")
        boolean mobilityAssistance, // 이동 도움 유무
        @Schema(description = "특이사항 입력", example = "평소보다 컨디션이 좋으셨다.")
        String physicalNote // 특이사항 입력
) {
}
