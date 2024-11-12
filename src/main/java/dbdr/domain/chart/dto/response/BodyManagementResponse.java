package dbdr.domain.chart.dto.response;

public record BodyManagementResponse(
        Long id,
        boolean wash, // 세면 유무
        boolean bath, // 목욕 유무
        String mealType, // 식사 종류
        String intakeAmount, // 섭취량
        String physicalRestroom, // 화장실 횟수
        boolean hasWalked, // 산책
        boolean positionChangeRequired, // 체위 변경 유무
        boolean mobilityAssistance, // 이동 도움 유무
        String physicalNote // 특이사항 입력
) {
}
