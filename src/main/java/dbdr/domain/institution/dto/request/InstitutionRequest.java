package dbdr.domain.institution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstitutionRequest(
    @NotNull(message = "기관 번호는 필수 항목입니다.")
    Long institutionNumber,
    @NotBlank(message = "기관 이름은 필수 항목입니다.")
    String institutionName
) {

}
