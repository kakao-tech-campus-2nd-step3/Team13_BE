package dbdr.domain.guardian.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GuardianResponse(
    @NotBlank(message = "전화번호를 입력하세요.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX형식으로 입력해주세요.")
    String phone,
    @NotBlank(message = "이름을 입력하세요.")
    String name,
    boolean isActive) {
}
