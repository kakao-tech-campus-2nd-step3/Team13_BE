package dbdr.chart;

import static org.assertj.core.api.Assertions.assertThat;

import dbdr.chart.domain.BodyManagement;
import dbdr.chart.domain.Chart;
import dbdr.chart.domain.HealthBloodPressure;
import dbdr.chart.domain.NursingManagement;
import dbdr.chart.domain.PhysicalClear;
import dbdr.chart.domain.PhysicalMeal;
import dbdr.chart.domain.PhysicalWalk;
import dbdr.chart.dto.ChartMapper;
import dbdr.chart.dto.request.BodyManagementRequest;
import dbdr.chart.dto.request.ChartDetailRequest;
import dbdr.chart.dto.request.CognitiveManagementRequest;
import dbdr.chart.dto.request.NursingManagementRequest;
import dbdr.chart.dto.request.RecoveryTrainingRequest;
import dbdr.chart.dto.response.BodyManagementResponse;
import dbdr.chart.dto.response.ChartDetailResponse;
import dbdr.chart.dto.response.NursingManagementResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ChartMapperTest {

    private final ChartMapper chartMapper = Mappers.getMapper(ChartMapper.class);

    @Test
    void testToResponse_chartToChartDetailResponse() {
        // given
        Chart chart = new Chart();

        // when
        ChartDetailResponse response = chartMapper.toResponse(chart);

        // then
        assertThat(response).isNotNull();
        assertThat(response.chartId()).isEqualTo(chart.getId());
    }

    @Test
    void testToEntity_chartDetailRequestToChart() {
        // given
        ChartDetailRequest request = new ChartDetailRequest(
                new BodyManagementRequest(true, false, "Lunch", "Full", 3, true, false, "Good"),
                new NursingManagementRequest(120, 80, "36.5", "All good"),
                new CognitiveManagementRequest(true, "No issues"),
                new RecoveryTrainingRequest("Physical Therapy", true, "Completed")
        );

        // when
        Chart chart = chartMapper.toEntity(request);

        // then
        assertThat(chart).isNotNull();
        assertThat(chart.getNursingManagement()).isNotNull();
        assertThat(chart.getBodyManagement().getPhysicalMeal().getMealType()).isEqualTo(
                request.bodyManagement().mealType());
    }

    @Test
    void testToResponse_bodyManagementToBodyManagementResponse() {
        // given
        PhysicalClear physicalClear = new PhysicalClear(true, false);
        PhysicalMeal physicalMeal = new PhysicalMeal("Lunch", "Full");
        PhysicalWalk physicalWalk = new PhysicalWalk(true, false);
        BodyManagement bodyManagement = new BodyManagement(physicalMeal, physicalWalk, physicalClear, 3, "Good");

        // when
        BodyManagementResponse response = chartMapper.toResponse(bodyManagement);

        // then
        assertThat(response).isNotNull();
        assertThat(response.wash()).isEqualTo(bodyManagement.getPhysicalClear().isWash());
        assertThat(response.bath()).isEqualTo(bodyManagement.getPhysicalClear().isBath());
        assertThat(response.mealType()).isEqualTo(bodyManagement.getPhysicalMeal().getMealType());
        assertThat(response.intakeAmount()).isEqualTo(bodyManagement.getPhysicalMeal().getIntakeAmount());
    }

    @Test
    void testToEntity_bodyManagementRequestToBodyManagement() {
        // given
        BodyManagementRequest request = new BodyManagementRequest(
                true, false, "Lunch", "Full", 3, true, false, "Good"
        );

        // when
        BodyManagement bodyManagement = chartMapper.toEntity(request);

        // then
        assertThat(bodyManagement).isNotNull();
        assertThat(bodyManagement.getPhysicalClear().isWash()).isEqualTo(request.wash());
        assertThat(bodyManagement.getPhysicalClear().isBath()).isEqualTo(request.bath());
        assertThat(bodyManagement.getPhysicalMeal().getMealType()).isEqualTo(request.mealType());
        assertThat(bodyManagement.getPhysicalMeal().getIntakeAmount()).isEqualTo(request.intakeAmount());
    }

    @Test
    void testToResponse_nursingManagementToNursingManagementResponse() {
        // given
        HealthBloodPressure healthBloodPressure = new HealthBloodPressure(120, 80);
        NursingManagement nursingManagement = new NursingManagement(healthBloodPressure, "36.5", "All good");

        // when
        NursingManagementResponse response = chartMapper.toResponse(nursingManagement);

        // then
        assertThat(response).isNotNull();
        assertThat(response.systolic()).isEqualTo(nursingManagement.getHealthBloodPressure().getSystolic());
        assertThat(response.diastolic()).isEqualTo(nursingManagement.getHealthBloodPressure().getDiastolic());
    }

    @Test
    void testToEntity_nursingManagementRequestToNursingManagement() {
        // given
        NursingManagementRequest request = new NursingManagementRequest(120, 80, "36.5", "All good");

        // when
        NursingManagement nursingManagement = chartMapper.toEntity(request);

        // then
        assertThat(nursingManagement).isNotNull();
        assertThat(nursingManagement.getHealthBloodPressure().getSystolic()).isEqualTo(request.systolic());
        assertThat(nursingManagement.getHealthBloodPressure().getDiastolic()).isEqualTo(request.diastolic());
    }
}
