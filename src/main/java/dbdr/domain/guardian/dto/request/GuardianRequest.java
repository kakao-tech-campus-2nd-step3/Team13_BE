package dbdr.domain.guardian.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GuardianRequest(
    @Schema(description = "보호자의 휴대폰 번호", example = "01012341234")
    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX형식으로 입력해주세요.")
    String phone,
    @Schema(description = "보호자의 이름", example = "박준협")
    @NotBlank(message = "이름은 필수 항목입니다.")
    String name,
    @Schema(description = "보호자의 비밀번호", example = "abcdefg")
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    String loginPassword,
    @Schema(description = "요양원 아이디", example = "11")
    @NotNull(message = "요양원 아이디는 필수 항목입니다.")
    Long institutionId) {

}
