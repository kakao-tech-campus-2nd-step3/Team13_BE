package dbdr.domain.careworker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareworkerResponseDTO {

    private Long id;
    private Long institutionId;
    private String name;
    private String email;
    private String phone;
}
