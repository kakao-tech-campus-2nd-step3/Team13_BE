package dbdr.domain.chart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecoveryTrainingRequest(
        @Schema(description = "회복 프로그램 이름", example = "회복")
        String recoveryProgram, // 회복 프로그램 이름
        @Schema(description = "회복훈련 완료 여부", example = "true")
        boolean recoveryTraining, // 회복훈련 완료 여부
        @Schema(description = "인지훈련 제공 여부", example = "true")
        boolean isCognitiveTrainingProvided, // 인지훈련 제공 여부
        @Schema(description = "물리치료 제공 여부", example = "true")
        boolean isPhysicalTherapyProvided, // 물리치료 제공 여부
        @Schema(description = "회복훈련 특이사항", example = "없음")
        String recoveryNote // 회복훈련 특이사항
) {
}
