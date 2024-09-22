package dbdr.domain;

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

    @Column(name = "care_level", nullable = false)
    private String careLevel;

    @Column(name = "care_number", nullable = false, unique = true)
    private String careNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private String institution;

    @Column(name = "institution_number", nullable = false)
    private Long institutionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "careworker_id")
    private Careworker careworker;

    public void updateRecipient(String name, LocalDate birth, String gender, String careLevel, String careNumber,
                                LocalDate startDate, String institution, Long institutionNumber, Careworker careworker) {
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
}