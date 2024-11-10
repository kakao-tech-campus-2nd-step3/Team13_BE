package dbdr.domain.chart.dto.response;

public record NursingManagementResponse(
        Long id,
        String systolic,  // 혈압 최고
        String diastolic, // 혈압 최저
        String healthTemperature, // 체온
        boolean healthCareProvided, // 건강 관리 제공 여부
        boolean nursingCareProvided, // 간호 관리 제공 여부
        boolean emergencyCareProvided,
        String healthNote // 건강 및 간호관리 특이사항
) {
}
