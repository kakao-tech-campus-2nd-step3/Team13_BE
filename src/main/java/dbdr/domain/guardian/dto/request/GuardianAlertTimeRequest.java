package dbdr.domain.guardian.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;

public record GuardianAlertTimeRequest(
    @Schema(description = "보호자의 이름", example = "박준협")
    @NotBlank(message = "이름은 필수 항목입니다.")
    String name,

    @Schema(description = "보호자의 휴대폰 번호", example = "01012341234")
    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "010\\d{8}", message = "010XXXXXXXX 형식으로 입력해주세요.")
    String phone,

    @Schema(description = "보호자의 알림 시간", example = "18:00:00")
    @NotNull(message = "알람 시간은 필수 항목입니다.")
    LocalTime alertTime
) {
}
