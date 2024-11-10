package dbdr.domain.chart.dto.response;

public record ChartDetailResponse(
        Long chartId,
        Long recipientId,
        String conditionDisease,
        BodyManagementResponse bodyManagement,
        NursingManagementResponse nursingManagement,
        CognitiveManagementResponse cognitiveManagement,
        RecoveryTrainingResponse recoveryTraining
) {
}
