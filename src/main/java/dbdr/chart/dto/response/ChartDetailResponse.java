package dbdr.chart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChartDetailResponse {
    private Long chartId;
    private BodyManagementResponse bodyManagement;
    private NursingManagementResponse nursingManagement;
    private CognitiveManagementResponse cognitiveManagement;
    private RecoveryTrainingResponse recoveryTraining;
}
