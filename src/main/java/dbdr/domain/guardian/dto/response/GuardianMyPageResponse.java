package dbdr.domain.guardian.dto.response;

import java.time.LocalTime;

public record GuardianMyPageResponse(String name, String phone,
                                     LocalTime alertTime) {

}
