package dbdr.domain.chart.dto.response;

public record BodyManagementResponse(
        Long id,
        boolean wash, // 세면 유무
        boolean bath, // 목욕 유무
        String mealType, // 식사 종류
        String intakeAmount, // 섭취량
        int physicalRestroom, // 화장실 횟수
        boolean has_walked, // 산책
        boolean has_companion, // 외출 동행
        String physicalNote // 특이사항 입력
) {
}
