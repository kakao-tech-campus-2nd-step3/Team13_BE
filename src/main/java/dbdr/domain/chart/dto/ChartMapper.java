package dbdr.domain.chart.dto;

import dbdr.domain.chart.dto.request.BodyManagementRequest;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.request.CognitiveManagementRequest;
import dbdr.domain.chart.dto.request.NursingManagementRequest;
import dbdr.domain.chart.dto.request.RecoveryTrainingRequest;
import dbdr.domain.chart.dto.response.BodyManagementResponse;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.CognitiveManagementResponse;
import dbdr.domain.chart.dto.response.NursingManagementResponse;
import dbdr.domain.chart.dto.response.RecoveryTrainingResponse;
import dbdr.domain.chart.entity.BodyManagement;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.entity.CognitiveManagement;
import dbdr.domain.chart.entity.NursingManagement;
import dbdr.domain.chart.entity.RecoveryTraining;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ChartMapper {
    @Mappings({
            @Mapping(source = "bodyManagement", target = "bodyManagement"),
            @Mapping(target = "chartId", source = "id")})
    ChartDetailResponse toResponse(Chart chart);

    @Mappings({
        @Mapping(target = "bodyManagement", source = "bodyManagement"),
        @Mapping(target = "nursingManagement", source = "nursingManagement"),
        @Mapping(target = "cognitiveManagement", source = "cognitiveManagement"),
        @Mapping(target = "recoveryTraining", source = "recoveryTraining"),
        @Mapping(target = "conditionDisease", source = "conditionDisease"),
        @Mapping(target = "recipient", source = "recipient")
    })
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
            @Mapping(target = "healthBloodPressure.systolic", source = "systolic"),
            @Mapping(target = "healthBloodPressure.diastolic", source = "diastolic")
    })
    NursingManagement toEntity(NursingManagementRequest request);

    // CognitiveManagement 매핑
    CognitiveManagementResponse toResponse(CognitiveManagement cognitiveManagement);

    CognitiveManagement toEntity(CognitiveManagementRequest request);

    // RecoveryTraining 매핑
    RecoveryTrainingResponse toResponse(RecoveryTraining recoveryTraining);

    RecoveryTraining toEntity(RecoveryTrainingRequest request);
}
