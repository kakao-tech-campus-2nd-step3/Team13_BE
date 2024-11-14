package dbdr.domain.careworker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CareworkerResponse {
    private Long id;
    private Long institutionId;
    private String name;
    private String email;
    private String phone;
}
