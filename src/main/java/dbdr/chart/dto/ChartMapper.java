package dbdr.chart.dto;

import dbdr.chart.domain.BodyManagement;
import dbdr.chart.domain.Chart;
import dbdr.chart.domain.CognitiveManagement;
import dbdr.chart.domain.NursingManagement;
import dbdr.chart.domain.RecoveryTraining;
import dbdr.chart.dto.request.BodyManagementRequest;
import dbdr.chart.dto.request.ChartDetailRequest;
import dbdr.chart.dto.request.CognitiveManagementRequest;
import dbdr.chart.dto.request.NursingManagementRequest;
import dbdr.chart.dto.request.RecoveryTrainingRequest;
import dbdr.chart.dto.response.BodyManagementResponse;
import dbdr.chart.dto.response.ChartDetailResponse;
import dbdr.chart.dto.response.CognitiveManagementResponse;
import dbdr.chart.dto.response.NursingManagementResponse;
import dbdr.chart.dto.response.RecoveryTrainingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface ChartMapper {

    @Mapping(target = "chartId", source = "id")
    ChartDetailResponse toResponse(Chart chart);

    @Mapping(target = "id", ignore = true)
    Chart toEntity(ChartDetailRequest request);

    // BodyManagement 매핑
    @Mappings({
            @Mapping(target = "wash", source = "physicalClear.wash"),
            @Mapping(target = "bath", source = "physicalClear.bath"),
            @Mapping(target = "mealType", source = "physicalMeal.mealType"),
            @Mapping(target = "intakeAmount", source = "physicalMeal.intakeAmount"),
            @Mapping(target = "has_walked", source = "physicalWalk.hasWalked"),
            @Mapping(target = "has_companion", source = "physicalWalk.hasCompanion"),
    })
    BodyManagementResponse toResponse(BodyManagement bodyManagement);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "physicalClear.wash", source = "wash"),
            @Mapping(target = "physicalClear.bath", source = "bath"),
            @Mapping(target = "physicalMeal.mealType", source = "mealType"),
            @Mapping(target = "physicalMeal.intakeAmount", source = "intakeAmount"),
            @Mapping(target = "physicalWalk.hasWalked", source = "has_walked"),
            @Mapping(target = "physicalWalk.hasCompanion", source = "has_companion")
    })
    BodyManagement toEntity(BodyManagementRequest request);

    // NursingManagement 매핑
    @Mappings({
            @Mapping(target = "systolic", source = "healthBloodPressure.systolic"),
            @Mapping(target = "diastolic", source = "healthBloodPressure.diastolic")
    })
    NursingManagementResponse toResponse(NursingManagement nursingManagement);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "healthBloodPressure.systolic", source = "systolic"),
            @Mapping(target = "healthBloodPressure.diastolic", source = "diastolic")
    })
    NursingManagement toEntity(NursingManagementRequest request);

    // CognitiveManagement 매핑
    CognitiveManagementResponse toResponse(CognitiveManagement cognitiveManagement);

    @Mapping(target = "id", ignore = true)
    CognitiveManagement toEntity(CognitiveManagementRequest request);

    // RecoveryTraining 매핑
    RecoveryTrainingResponse toResponse(RecoveryTraining recoveryTraining);

    @Mapping(target = "id", ignore = true)
    RecoveryTraining toEntity(RecoveryTrainingRequest request);
}
