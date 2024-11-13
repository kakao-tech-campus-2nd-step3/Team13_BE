package dbdr.domain.recipient.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.dto.request.RecipientUpdateCareworkerRequest;
import dbdr.domain.recipient.dto.request.RecipientUpdateInstitutionRequest;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id")
    private Guardian guardian;


    @Builder
    public Recipient(String name,
                     LocalDate birth,
                     String gender,
                     String careLevel,
                     String careNumber,
                     LocalDate startDate,
                     Institution institution,
                     Long institutionNumber,
                     Careworker careworker,
                     Guardian guardian) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.careLevel = careLevel;
        this.careNumber = careNumber;
        this.startDate = startDate;
        this.institution = institution;
        this.institutionNumber = institutionNumber;
        this.careworker = careworker;
        this.guardian = guardian;
    }


    public Recipient(RecipientRequest dto, Careworker careworker, Guardian guardian) {
        this.name = dto.getName();
        this.birth = dto.getBirth();
        this.gender = dto.getGender();
        this.careLevel = dto.getCareLevel();
        this.careNumber = dto.getCareNumber();
        this.startDate = dto.getStartDate();
        this.institution = careworker.getInstitution();
        this.institutionNumber = careworker.getInstitution().getInstitutionNumber();
        this.careworker = careworker;
        this.guardian = guardian;
    }


    public Recipient(RecipientRequest dto, Institution institution, Careworker careworker) {
        this.name = dto.getName();
        this.birth = dto.getBirth();
        this.gender = dto.getGender();
        this.careLevel = dto.getCareLevel();
        this.careNumber = dto.getCareNumber();
        this.startDate = dto.getStartDate();
        this.institution = institution;
        this.institutionNumber = institution.getInstitutionNumber();
        this.careworker = careworker;
    }

    public Recipient(RecipientRequest dto, Institution institution, Careworker careworker, Guardian guardian) {
        this.name = dto.getName();
        this.birth = dto.getBirth();
        this.gender = dto.getGender();
        this.careLevel = dto.getCareLevel();
        this.careNumber = dto.getCareNumber();
        this.startDate = dto.getStartDate();
        this.institution = institution;
        this.institutionNumber = institution.getInstitutionNumber();
        this.careworker = careworker;
        this.guardian = guardian;
    }


    public void updateRecipient(RecipientRequest recipientDTO) {
        this.name = recipientDTO.getName();
        this.birth = recipientDTO.getBirth();
        this.gender = recipientDTO.getGender();
        this.careLevel = recipientDTO.getCareLevel();
        this.careNumber = recipientDTO.getCareNumber();
        this.startDate = recipientDTO.getStartDate();
    }

    public void updateRecipient(RecipientUpdateCareworkerRequest recipientDTO) {
        this.name = recipientDTO.getName();
        this.birth = recipientDTO.getBirth();
        this.gender = recipientDTO.getGender();
        this.careLevel = recipientDTO.getCareLevel();
        this.careNumber = recipientDTO.getCareNumber();
        this.startDate = recipientDTO.getStartDate();
    }//요양보호사용

    public void updateRecipientForInstitution(RecipientUpdateInstitutionRequest recipientDTO, Careworker careworker, Guardian guardian) {
        this.name = recipientDTO.getName();
        this.birth = recipientDTO.getBirth();
        this.gender = recipientDTO.getGender();
        this.careLevel = recipientDTO.getCareLevel();
        this.careNumber = recipientDTO.getCareNumber();
        this.startDate = recipientDTO.getStartDate();
        this.careworker = careworker;
        this.guardian = guardian;
    }//요양원용

    public void updateRecipientForAdmin(RecipientRequest recipientDTO, Institution institution, Careworker careworker, Guardian guardian) {
        this.institution = institution;
        this.institutionNumber = recipientDTO.getInstitutionNumber();
        this.careworker = careworker;
        this.guardian = guardian;
    } //관리자용

}
