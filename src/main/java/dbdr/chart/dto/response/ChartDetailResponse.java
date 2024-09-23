package dbdr.chart.dto.response;

import dbdr.chart.domain.BodyManagement;
import dbdr.chart.domain.Chart;
import dbdr.chart.domain.CognitiveManagement;
import dbdr.chart.domain.NursingManagement;
import dbdr.chart.domain.RecoveryTraining;
import java.util.Optional;
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

    // Chart 엔티티를 Response DTO로 변환하는 메서드
    public static ChartDetailResponse fromEntity(Chart chart) {
        return ChartDetailResponse.builder()
                .chartId(chart.getId())
                .bodyManagement(mapBodyManagement(chart.getBodyManagement()))
                .nursingManagement(mapNursingManagement(chart.getNursingManagement()))
                .cognitiveManagement(mapCognitiveManagement(chart.getCognitiveManagement()))
                .recoveryTraining(mapRecoveryTraining(chart.getRecoveryTraining()))
                .build();
    }

    private static BodyManagementResponse mapBodyManagement(BodyManagement bodyManagement) {
        return Optional.ofNullable(bodyManagement)
                .map(bm -> BodyManagementResponse.builder()
                        .id(bm.getId())
                        .wash(bm.getPhysicalClear().isWash())
                        .bath(bm.getPhysicalClear().isBath())
                        .mealType(bm.getPhysicalMeal().getMealType())
                        .intakeAmount(bm.getPhysicalMeal().getIntakeAmount())
                        .physicalRestroom(bm.getPhysicalRestroom())
                        .has_walked(bm.getPhysicalWalk().isHasWalked())
                        .has_companion(bm.getPhysicalWalk().isHasCompanion())
                        .physicalNote(bm.getPhysicalNote())
                        .build())
                .orElse(null);
    }

    private static NursingManagementResponse mapNursingManagement(NursingManagement nursingManagement) {
        return Optional.ofNullable(nursingManagement)
                .map(nm -> NursingManagementResponse.builder()
                        .id(nm.getId())
                        .systolic(nm.getHealthBloodPressure().getSystolic())
                        .diastolic(nm.getHealthBloodPressure().getDiastolic())
                        .healthTemperature(nm.getHealthTemperature())
                        .healthNote(nm.getHealthNote())
                        .build())
                .orElse(null);
    }

    private static CognitiveManagementResponse mapCognitiveManagement(CognitiveManagement cognitiveManagement) {
        return Optional.ofNullable(cognitiveManagement)
                .map(cm -> CognitiveManagementResponse.builder()
                        .id(cm.getId())
                        .cognitiveHelp(cm.isCognitiveHelp())
                        .cognitiveNote(cm.getCognitiveNote())
                        .build())
                .orElse(null);
    }

    private static RecoveryTrainingResponse mapRecoveryTraining(RecoveryTraining recoveryTraining) {
        return Optional.ofNullable(recoveryTraining)
                .map(rt -> RecoveryTrainingResponse.builder()
                        .id(rt.getId())
                        .recoveryProgram(rt.getRecoveryProgram())
                        .recoveryTraining(rt.isRecoveryTraining())
                        .recoveryNote(rt.getRecoveryNote())
                        .build())
                .orElse(null);
    }
}
