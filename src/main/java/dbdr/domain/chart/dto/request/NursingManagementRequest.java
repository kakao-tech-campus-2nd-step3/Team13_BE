package dbdr.domain.chart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record NursingManagementRequest(
        @Schema(description = "혈압 최고", example = "300")
        int systolic,  // 혈압 최고
        @Schema(description = "혈압 최저", example = "200")
        int diastolic, // 혈압 최저
        @Schema(description = "체온", example = "32도")
        String healthTemperature, // 체온
        @Schema(description = "건강 관리 제공 여부", example = "true")
        boolean isHealthCareProvided, // 건강 관리 제공 여부
        @Schema(description = "간호 관리 제공 여부", example = "true")
        boolean isNursingCareProvided, // 간호 관리 제공 여부
        @Schema(description = "응급 관리 제공 여부", example = "true")
        boolean isEmergencyCareProvided, //  응급 관리 제공 여부
        @Schema(description = "건강 및 간호관리 특이사항", example = "없음")
        String healthNote // 건강 및 간호관리 특이사항
) {
}
