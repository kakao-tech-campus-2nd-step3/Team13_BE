package dbdr.domain.chart.dto.response;

public record RecoveryTrainingResponse(
        Long id,
        String recoveryProgram, // 회복 프로그램 이름
        boolean recoveryTraining, // 회복훈련 완료 여부
        boolean isCognitiveTrainingProvided, // 인지훈련 제공 여부
        boolean isPhysicalTherapyProvided, // 물리치료 제공 여부
        String recoveryNote // 회복훈련 특이사항
) {
}
