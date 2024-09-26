package dbdr.domain.chart.dto.request;

public record ChartDetailRequest(
        BodyManagementRequest bodyManagement,
        NursingManagementRequest nursingManagement,
        CognitiveManagementRequest cognitiveManagement,
        RecoveryTrainingRequest recoveryTraining
) {
}
