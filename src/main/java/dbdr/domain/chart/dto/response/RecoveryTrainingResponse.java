package dbdr.domain.chart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecoveryTrainingResponse(
    Long id,
    @JsonProperty("회복 프로그램 이름") String recoveryProgram, // 회복 프로그램 이름
    @JsonProperty("회복훈련 완료 여부") boolean recoveryTraining, // 회복훈련 완료 여부
    @JsonProperty("회복훈련 특이사항") String recoveryNote // 회복훈련 특이사항
) {
}
