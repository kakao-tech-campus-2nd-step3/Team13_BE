package dbdr.domain.guardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GuardianRequest(
    @NotBlank(message = "전화번호를 입력하세요.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX형식으로 입력해주세요.")
    String phone,
    @NotBlank(message = "이름을 입력하세요.")
    String name,
    @NotBlank(message = "비밀번호를 입력하세요.")
    String loginPassword) {

}
