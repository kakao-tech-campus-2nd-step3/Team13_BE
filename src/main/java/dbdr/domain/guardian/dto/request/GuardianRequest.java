package dbdr.domain.guardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GuardianRequest(
    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX형식으로 입력해주세요.")
    String phone,
    @NotBlank(message = "이름은 필수 항목입니다.")
    String name) {

}
