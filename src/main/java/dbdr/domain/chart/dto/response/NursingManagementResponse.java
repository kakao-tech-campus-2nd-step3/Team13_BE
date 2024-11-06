package dbdr.domain.chart.dto.response;

public record NursingManagementResponse(
        Long id,
        int systolic,  // 혈압 최고
        int diastolic, // 혈압 최저
        String healthTemperature, // 체온
        boolean isHealthCareProvided, // 건강 관리 제공 여부
        boolean isNursingCareProvided, // 간호 관리 제공 여부
        boolean isEmergencyCareProvided,
        String healthNote // 건강 및 간호관리 특이사항
) {
}
