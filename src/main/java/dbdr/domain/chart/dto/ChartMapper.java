package dbdr.domain.chart.dto;

import dbdr.domain.chart.dto.request.BodyManagementRequest;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.request.CognitiveManagementRequest;
import dbdr.domain.chart.dto.request.NursingManagementRequest;
import dbdr.domain.chart.dto.request.RecoveryTrainingRequest;
import dbdr.domain.chart.dto.response.BodyManagementResponse;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.ChartOverviewResponse;
import dbdr.domain.chart.dto.response.CognitiveManagementResponse;
import dbdr.domain.chart.dto.response.NursingManagementResponse;
import dbdr.domain.chart.dto.response.RecoveryTrainingResponse;
import dbdr.domain.chart.entity.BodyManagement;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.entity.CognitiveManagement;
import dbdr.domain.chart.entity.NursingManagement;
import dbdr.domain.chart.entity.RecoveryTraining;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.service.RecipientService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ChartMapper {
    @Autowired
    private RecipientService recipientService;

    @Mappings({
            @Mapping(source = "bodyManagement", target = "bodyManagement"),
            @Mapping(target = "chartId", source = "id"),
            @Mapping(target = "recipientId", source = "recipient.id"),})
    public abstract ChartDetailResponse toResponse(Chart chart);

    @Mappings({
            @Mapping(target = "bodyManagement", source = "bodyManagement"),
            @Mapping(target = "nursingManagement", source = "nursingManagement"),
            @Mapping(target = "cognitiveManagement", source = "cognitiveManagement"),
            @Mapping(target = "recoveryTraining", source = "recoveryTraining"),
            @Mapping(target = "conditionDisease", source = "conditionDisease"),
            @Mapping(target = "recipient", source = "recipientId")
    })
    public abstract Chart toEntity(ChartDetailRequest request);

    @Mapping(target = "chartId", source = "id")
    @Mapping(target = "recipientName", source = "recipient.name")
    @Mapping(target = "chartDate", source = "createdAt")
    public abstract ChartOverviewResponse toOverviewResponse(Chart chart);

    protected Recipient mapRecipient(Long recipientId) {
        return recipientService.findRecipientById(recipientId);
    }

    // BodyManagement 매핑
    @Mappings({
            @Mapping(target = "wash", source = "physicalClear.wash"),
            @Mapping(target = "bath", source = "physicalClear.bath"),
            @Mapping(target = "mealType", source = "physicalMeal.mealType"),
            @Mapping(target = "intakeAmount", source = "physicalMeal.intakeAmount"),
            @Mapping(target = "physicalRestroom", source = "physicalRestroom")
    })
    public abstract BodyManagementResponse toResponse(BodyManagement bodyManagement);

    @Mappings({
            @Mapping(target = "physicalClear.wash", source = "wash"),
            @Mapping(target = "physicalClear.bath", source = "bath"),
            @Mapping(target = "physicalMeal.mealType", source = "mealType"),
            @Mapping(target = "physicalMeal.intakeAmount", source = "intakeAmount")
    })
    public abstract BodyManagement toEntity(BodyManagementRequest request);

    // NursingManagement 매핑
    @Mappings({
            @Mapping(target = "systolic", source = "healthBloodPressure.systolic"),
            @Mapping(target = "diastolic", source = "healthBloodPressure.diastolic")
    })
    public abstract NursingManagementResponse toResponse(NursingManagement nursingManagement);

    @Mappings({
            @Mapping(target = "healthBloodPressure.systolic", source = "systolic"),
            @Mapping(target = "healthBloodPressure.diastolic", source = "diastolic")
    })
    public abstract NursingManagement toEntity(NursingManagementRequest request);

    // CognitiveManagement 매핑
    public abstract CognitiveManagementResponse toResponse(CognitiveManagement cognitiveManagement);

    public abstract CognitiveManagement toEntity(CognitiveManagementRequest request);

    // RecoveryTraining 매핑
    public abstract RecoveryTrainingResponse toResponse(RecoveryTraining recoveryTraining);

    public abstract RecoveryTraining toEntity(RecoveryTrainingRequest request);
}
