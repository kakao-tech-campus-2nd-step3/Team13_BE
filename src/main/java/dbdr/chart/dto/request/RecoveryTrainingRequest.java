package dbdr.chart.dto.request;

public record RecoveryTrainingRequest(
        String recoveryProgram, // 회복 프로그램 이름
        boolean recoveryTraining, // 회복훈련 완료 여부
        String recoveryNote // 회복훈련 특이사항
) {
}
