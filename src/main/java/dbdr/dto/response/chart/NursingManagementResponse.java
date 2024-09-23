package dbdr.dto.response.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NursingManagementResponse {
    private Long id;

    private int systolic;  // 혈압 최고
    private int diastolic; // 혈압 최저

    private String healthTemperature; // 체온

    private String healthNote; // 건강 및 간호관리 특이사항
}
