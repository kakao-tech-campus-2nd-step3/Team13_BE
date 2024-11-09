package dbdr.domain.careworker.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Getter
@AllArgsConstructor
public class CareworkerMyPageResponseDTO {

    private String name;
    private String phone;
    private String institutionName;
    private String loginId;
    private Set<DayOfWeek> workingDays;
    private LocalTime alertTime;
}