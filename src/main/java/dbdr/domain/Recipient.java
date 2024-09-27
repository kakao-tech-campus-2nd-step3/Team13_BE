package dbdr.domain;

import dbdr.dto.request.RecipientRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipient")
@SQLDelete(sql = "UPDATE recipient SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Recipient extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String careLevel;

    @Column(nullable = false, unique = true)
    private String careNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private Long institutionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "careworker_id")
    private Careworker careworker;

    public void updateRecipient(RecipientRequestDTO recipientDTO) {
        this.name = recipientDTO.getName();
        this.birth = recipientDTO.getBirth();
        this.gender = recipientDTO.getGender();
        this.careLevel = recipientDTO.getCareLevel();
        this.careNumber = recipientDTO.getCareNumber();
        this.startDate = recipientDTO.getStartDate();
        this.institution = recipientDTO.getInstitution();
        this.institutionNumber = recipientDTO.getInstitutionNumber();
        //this.careworker = careworker;
    }

}