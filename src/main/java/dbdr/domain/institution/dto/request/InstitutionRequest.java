package dbdr.domain.institution.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstitutionRequest(
    @Schema(description = "기관 번호", example = "12345678")
    @NotNull(message = "기관 번호는 필수 항목입니다.")
    Long institutionNumber,
    @Schema(description = "기관 이름", example = "사랑돌봄요양기관")
    @NotBlank(message = "기관 이름은 필수 항목입니다.")
    String institutionName,
    @Schema(description = "기관 로그인 Id",example = "love1234")
    @NotBlank(message = "기관 로그인 Id는 필수 항목입니다.")
    String institutionLoginId,
    @Schema(description = "기관 로그인 비밀번호",example = "12345678")
    @NotBlank(message = "기관 로그인 비밀번호는 필수 항목입니다.")
    String institutionLoginPassword
) {

}
