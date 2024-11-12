package dbdr.domain.careworker.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@AllArgsConstructor
public class CareworkerUpdateRequest {

    @NotNull(message = "근무일은 필수 항목입니다.")
    @Schema(description = "근무 요일 목록", example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]")
    private Set<DayOfWeek> workingDays;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "알림 시간은 필수 항목입니다.")
    @Schema(description = "알림 시간 (HH:mm 형식)", example = "17:00")
    private LocalTime alertTime;
}
