package dbdr.domain.guardian.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record GuardianMyPageRequest(
    @Schema(description = "보호자의 알림 시간", example = "09:00:00")
    LocalTime alertTime,

    @Schema(description = "SMS 수신 동의 여부", example = "true")
    boolean smsSubscription,

    @Schema(description = "LINE 수신 동의 여부", example = "true")
    boolean lineSubscription
) {
}
