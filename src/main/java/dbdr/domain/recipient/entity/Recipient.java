package dbdr.domain.recipient.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "recipients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE recipients SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Recipient extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column
    private String gender;

    @Column
    private String careLevel;

    @Column(nullable = false, unique = true)
    private String careNumber;

    @Column
    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Column
    private Long institutionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "careworker_id")
    private Careworker careworker;

    @Builder
    public Recipient(String name,
                     LocalDate birth,
                     String gender,
                     String careLevel,
                     String careNumber,
                     LocalDate startDate,
                     Institution institution,
                     Long institutionNumber,
                     Careworker careworker) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.careLevel = careLevel;
        this.careNumber = careNumber;
        this.startDate = startDate;
        this.institution = institution;
        this.institutionNumber = institutionNumber;
        this.careworker = careworker;
    }


    public void updateRecipient(RecipientRequestDTO recipientDTO) {
        this.name = recipientDTO.getName();
        this.birth = recipientDTO.getBirth();
        this.gender = recipientDTO.getGender();
        this.careLevel = recipientDTO.getCareLevel();
        this.careNumber = recipientDTO.getCareNumber();
        this.startDate = recipientDTO.getStartDate();
        // this.institution = recipientDTO.getInstitution(); 요양기관이랑 요양보호사에 대해서는 돌봄대상자가 변경 못한다.
        this.institutionNumber = recipientDTO.getInstitutionNumber();
        //this.careworker = careworker;
    }

}