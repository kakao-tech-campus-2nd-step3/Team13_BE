package dbdr.domain.guardians;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Comment;

public record GuardiansResponse(
    @Comment("보호자 전화번호") @NotBlank(message = "전화번호를 입력하세요.") String phone,
    @Comment("보호자 성명") @NotBlank(message = "이름을 입력하세요.") String name,
    @Comment("삭제 여부") boolean isActive) {

}
