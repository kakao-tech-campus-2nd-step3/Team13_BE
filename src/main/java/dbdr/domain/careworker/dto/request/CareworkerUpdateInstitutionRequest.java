package dbdr.domain.careworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareworkerUpdateInstitutionRequest {

    @Schema(description = "요양보호사 이름", example = "박경옥")
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @Schema(description = "요양보호사 이메일", example = "carecare20@email.com")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바르지 않은 형식입니다.")
    private String email;

    @Schema(description = "요양보호사 휴대폰 번호", example = "01057573332")
    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX형식으로 입력해주세요.")
    private String phone;

    // 요양원 권한 사용자는 institutionId를 변경할 수 없음
}
