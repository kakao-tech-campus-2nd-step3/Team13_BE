package dbdr.domain.chart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BodyManagementResponse(
    Long id,
    @JsonProperty("세면 유무") boolean wash, // 세면 유무
    @JsonProperty("목욕 유무") boolean bath, // 목욕 유무
    @JsonProperty("식사 종류") String mealType, // 식사 종류
    @JsonProperty("섭취량") String intakeAmount, // 섭취량
    @JsonProperty("화장실 횟수") int physicalRestroom, // 화장실 횟수
    @JsonProperty("산책 유무") boolean has_walked, // 산책
    @JsonProperty("외출 동행 유무") boolean has_companion, // 외출 동행
    @JsonProperty("특이사항 입력") String physicalNote // 특이사항 입력
) {
}
