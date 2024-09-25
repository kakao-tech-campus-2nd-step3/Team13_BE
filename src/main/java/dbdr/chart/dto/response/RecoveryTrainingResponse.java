package dbdr.chart.dto.response;

public record RecoveryTrainingResponse(
        Long id,
        String recoveryProgram, // 회복 프로그램 이름
        boolean recoveryTraining, // 회복훈련 완료 여부
        String recoveryNote // 회복훈련 특이사항
) {
}
