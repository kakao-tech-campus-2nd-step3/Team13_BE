package dbdr.chart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.dto.request.BodyManagementRequest;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.request.CognitiveManagementRequest;
import dbdr.domain.chart.dto.request.NursingManagementRequest;
import dbdr.domain.chart.dto.request.RecoveryTrainingRequest;
import dbdr.domain.chart.dto.response.BodyManagementResponse;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.ChartOverviewResponse;
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
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ChartMapperTest {

    @Autowired
    private ChartMapper chartMapper;

    @MockBean
    private RecipientService recipientService;

    private Recipient recipient;
    private Chart chartSpy;
    private Institution institution;

    @BeforeEach
    void setUp() {
        institution = Institution.builder()
                .institutionName("행복 요양원")
                .institutionNumber(100L)
                .build();

        recipient = Recipient.builder()
                .name("정말숙")
                .birth(LocalDate.of(1955, 5, 20))
                .gender("여성")
                .careLevel("Level 2")
                .careNumber("87654321")
                .startDate(LocalDate.now())
                .institution(institution)
                .institutionNumber(100L)
                .build();

        Chart chart = new Chart();
        chart.setRecipient(recipient);
        chart.setConditionDisease("고혈압");
        chart.setBodyManagement(new BodyManagement());
        chart.setNursingManagement(new NursingManagement());
        chart.setCognitiveManagement(null);
        chart.setRecoveryTraining(null);

        chartSpy = Mockito.spy(chart);

        doReturn(10L).when(chartSpy).getId();
        doReturn(LocalDateTime.of(2024, 10, 1, 12, 0)).when(chartSpy).getCreatedAt();
    }

    @Test
    void testToOverviewResponse_chartToChartOverviewResponse() {
        ChartOverviewResponse response = chartMapper.toOverviewResponse(chartSpy);
        assertThat(response).isNotNull();
        assertThat(response.chartId()).isEqualTo(10L);
        assertThat(response.recipientName()).isEqualTo("정말숙");
        assertThat(response.chartDate()).isEqualTo(LocalDate.of(2024, 10, 1));
    }

    @Test
    void testToResponse_chartToChartDetailResponse() {
        Chart chart = Mockito.spy(new Chart()); // Reuse spy creation logic here if needed
        doReturn(20L).when(chart).getId();  // Example of modifying the spy for a specific test case

        ChartDetailResponse response = chartMapper.toResponse(chart);
        assertThat(response).isNotNull();
        assertThat(response.chartId()).isEqualTo(20L);
    }

    @Test
    void testToEntity_chartDetailRequestToChart() {
        ChartDetailRequest request = new ChartDetailRequest(
                "독감",
                recipient.getId(),
                new BodyManagementRequest(true, false, "점심", "충분", "3", true, false, true, "좋음"),
                new NursingManagementRequest("120", "80", "36.5", true, true, true, "모든 것이 좋음"),
                new CognitiveManagementRequest(true, true, "문제 없음"),
                new RecoveryTrainingRequest("물리 치료", true, true, true, "완료")
        );

        when(recipientService.findRecipientById(recipient.getId())).thenReturn(recipient);
        Chart chart = chartMapper.toEntity(request);
        assertThat(chart).isNotNull();
        assertThat(chart.getConditionDisease()).isEqualTo(request.conditionDisease());
        assertThat(chart.getRecipient()).isNotNull();
        assertThat(chart.getRecipient().getName()).isEqualTo("정말숙");
    }

    @Test
    void testToResponse_bodyManagementToBodyManagementResponse() {
        PhysicalClear physicalClear = new PhysicalClear(true, false);
        PhysicalMeal physicalMeal = new PhysicalMeal("점심", "충분");
        BodyManagement bodyManagement = new BodyManagement(physicalMeal, physicalClear, "3", "좋음", true, true, true);

        BodyManagementResponse response = chartMapper.toResponse(bodyManagement);
        assertThat(response).isNotNull();
        assertThat(response.wash()).isEqualTo(physicalClear.isWash());
        assertThat(response.bath()).isEqualTo(physicalClear.isBath());
        assertThat(response.mealType()).isEqualTo(physicalMeal.getMealType());
        assertThat(response.intakeAmount()).isEqualTo(physicalMeal.getIntakeAmount());
    }

    @Test
    void testToEntity_bodyManagementRequestToBodyManagement() {
        BodyManagementRequest request = new BodyManagementRequest(
                true, false, "점심", "충분", "3", true, false, false, "좋음"
        );

        BodyManagement bodyManagement = chartMapper.toEntity(request);
        assertThat(bodyManagement).isNotNull();
        assertThat(bodyManagement.getPhysicalClear().isWash()).isEqualTo(request.wash());
        assertThat(bodyManagement.isHasWalked()).isEqualTo(request.hasWalked());
        assertThat(bodyManagement.getPhysicalClear().isBath()).isEqualTo(request.bath());
        assertThat(bodyManagement.getPhysicalMeal().getMealType()).isEqualTo(request.mealType());
        assertThat(bodyManagement.getPhysicalMeal().getIntakeAmount()).isEqualTo(request.intakeAmount());
    }

    @Test
    void testToResponse_nursingManagementToNursingManagementResponse() {
        HealthBloodPressure healthBloodPressure = new HealthBloodPressure("120", "80");
        NursingManagement nursingManagement = new NursingManagement(healthBloodPressure, "36.5", true, true, true,
                "모든 것이 좋음");

        NursingManagementResponse response = chartMapper.toResponse(nursingManagement);
        assertThat(response).isNotNull();
        assertThat(response.systolic()).isEqualTo(healthBloodPressure.getSystolic());
        assertThat(response.diastolic()).isEqualTo(healthBloodPressure.getDiastolic());
    }

    @Test
    void testToEntity_nursingManagementRequestToNursingManagement() {
        NursingManagementRequest request = new NursingManagementRequest("120", "80", "36.5", true, true, true,
                "모든 것이 좋음");

        NursingManagement nursingManagement = chartMapper.toEntity(request);
        assertThat(nursingManagement).isNotNull();
        assertThat(nursingManagement.getHealthBloodPressure().getSystolic()).isEqualTo(request.systolic());
        assertThat(nursingManagement.getHealthBloodPressure().getDiastolic()).isEqualTo(request.diastolic());
    }
}

