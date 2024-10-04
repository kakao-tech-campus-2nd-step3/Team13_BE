package dbdr.domain.chart.dto.response;

public record ChartDetailResponse(
        Long chartId,
        BodyManagementResponse bodyManagement,
        NursingManagementResponse nursingManagement,
        CognitiveManagementResponse cognitiveManagement,
        RecoveryTrainingResponse recoveryTraining
) {
}
