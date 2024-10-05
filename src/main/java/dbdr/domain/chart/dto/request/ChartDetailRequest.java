package dbdr.domain.chart.dto.request;

import dbdr.domain.recipient.entity.Recipient;

public record ChartDetailRequest(
	String conditionDisease,
	Recipient recipient,
	BodyManagementRequest bodyManagement,
	NursingManagementRequest nursingManagement,
	CognitiveManagementRequest cognitiveManagement,
	RecoveryTrainingRequest recoveryTraining
) {
}
