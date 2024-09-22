package dbdr.domain.guardians;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.Comment;

public record GuardiansRequest(
    @Comment("보호자 전화번호") @NotBlank(message = "전화번호를 입력하세요.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX형식으로 입력해주세요.") String phone,
    @Comment("보호자 성명") @NotBlank(message = "이름을 입력하세요.") String name) {

}
