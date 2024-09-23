package dbdr.chart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BodyManagementResponse {
    private Long id;

    private boolean wash; // 세면 유무
    private boolean bath; // 목욕 유무

    private String mealType; // 식사 종류
    private String intakeAmount; // 섭취량

    private int physicalRestroom; // 화장실 횟수

    private boolean has_walked; // 산책
    private boolean has_companion; // 외출 동행

    private String physicalNote; // 특이사항 입력
}
