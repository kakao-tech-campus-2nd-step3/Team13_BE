package dbdr.domain.chart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NursingManagementResponse(
    Long id,
    @JsonProperty("혈압 최고") int systolic,  // 혈압 최고
    @JsonProperty("혈압 최저") int diastolic, // 혈압 최저
    @JsonProperty("체온") String healthTemperature, // 체온
    @JsonProperty("건강 및 간호관리 특이사항") String healthNote // 건강 및 간호관리 특이사항
) {
}
