package dbdr.domain.recipient.dto.response;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.institution.entity.Institution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipientResponseDTO {

    private Long id;
    private String name;
    private LocalDate birth;
    private String gender;
    private String careLevel;
    private String careNumber;
    private LocalDate startDate;
    private String institution;
    private Long institutionNumber;
    private Long institutionId;
    private Long careworkerId;
}