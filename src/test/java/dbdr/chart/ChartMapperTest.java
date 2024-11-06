package dbdr.chart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.dto.request.BodyManagementRequest;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.request.CognitiveManagementRequest;
import dbdr.domain.chart.dto.request.NursingManagementRequest;
import dbdr.domain.chart.dto.request.RecoveryTrainingRequest;
import dbdr.domain.chart.dto.response.BodyManagementResponse;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.NursingManagementResponse;
import dbdr.domain.chart.entity.BodyManagement;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.entity.HealthBloodPressure;
import dbdr.domain.chart.entity.NursingManagement;
import dbdr.domain.chart.entity.PhysicalClear;
import dbdr.domain.chart.entity.PhysicalMeal;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.service.RecipientService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ChartMapperTest {

    @Autowired
    private ChartMapper chartMapper;

    @MockBean
    private RecipientService recipientService;


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
        Long recipientId = 1L;
        Institution institution = Institution.builder()
                .institutionName("HealthCare Institution")
                .institutionNumber(100L)
                .build();
        Recipient recipient = Recipient.builder()
                .name("John Doe")
                .birth(LocalDate.of(1950, 1, 1))
                .gender("Male")
                .careLevel("Level 1")
                .careNumber("12345678")
                .startDate(LocalDate.of(2020, 1, 1))
                .institution(institution)
                .institutionNumber(100L)
                .build();

        when(recipientService.findRecipientById(recipientId)).thenReturn(recipient);
        ChartDetailRequest request = new ChartDetailRequest(
                "Flu",
                recipientId,
                new BodyManagementRequest(true, false, "Lunch", "Full", 3, true, false, true, "Good"),
                new NursingManagementRequest(120, 80, "36.5", true, true, true, "All good"),
                new CognitiveManagementRequest(true, true, "No issues"),
                new RecoveryTrainingRequest("Physical Therapy", true, true, true, "Completed")
        );

        // when
        Chart chart = chartMapper.toEntity(request);

        // then
        assertThat(chart).isNotNull();
        assertThat(chart.getConditionDisease()).isEqualTo(request.conditionDisease());
        assertThat(chart.getRecipient()).isNotNull();
        assertThat(chart.getRecipient().getName()).isEqualTo("John Doe");
        assertThat(chart.getNursingManagement()).isNotNull();
        assertThat(chart.getBodyManagement().getPhysicalMeal().getMealType()).isEqualTo(
                request.bodyManagement().mealType());
    }

    @Test
    void testToResponse_bodyManagementToBodyManagementResponse() {
        // given
        PhysicalClear physicalClear = new PhysicalClear(true, false);
        PhysicalMeal physicalMeal = new PhysicalMeal("Lunch", "Full");

        BodyManagement bodyManagement = new BodyManagement(physicalMeal, physicalClear, 3, "Good", true, true, true);

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
                true, false, "Lunch", "Full", 3, true, false, false, "Good"
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
        NursingManagement nursingManagement = new NursingManagement(healthBloodPressure, "36.5", true, true, true,
                "All good");

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
        NursingManagementRequest request = new NursingManagementRequest(120, 80, "36.5", true, true, true, "All good");

        // when
        NursingManagement nursingManagement = chartMapper.toEntity(request);

        // then
        assertThat(nursingManagement).isNotNull();
        assertThat(nursingManagement.getHealthBloodPressure().getSystolic()).isEqualTo(request.systolic());
        assertThat(nursingManagement.getHealthBloodPressure().getDiastolic()).isEqualTo(request.diastolic());
    }
}
