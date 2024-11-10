package dbdr.domain.careworker.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.institution.entity.Institution;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "careworkers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE careworkers SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Careworker extends BaseEntity {

    private String loginPassword;

    @Column(nullable = false)
    @Pattern(regexp = "010\\d{8}")
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    // 근무일을 요일로 설정
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workingDays;

    @Column(nullable = true)
    private String lineUserId;

    @Column(nullable = true)
    private LocalTime alertTime;

    @Column(unique = true)
    private String email;

//    @Builder
//    public Careworker(Institution institution, String name, String email, String phone) {
//        this.institution = institution;
//        this.name = name;
//        this.email = email;
//        this.phone = phone;
//        this.alertTime = LocalTime.of(17, 0); // 오후 5시로 초기화
//    }

    @Builder
    public Careworker(String loginPassword, String phone, String name, Institution institution,
                      String email) {
        this.loginPassword = loginPassword;
        this.phone = phone;
        this.name = name;
        this.institution = institution;
        this.email = email;
    }

    public void updateCareworker(Careworker careworker) {
        this.name = careworker.getName();
        this.email = careworker.getEmail();
        this.phone = careworker.getPhone();
    }

    public void updateLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public void updateWorkingDays(Set<DayOfWeek> workingDays) {
        this.workingDays = workingDays;
    }

    public void updateAlertTime(LocalTime alertTime) {
        this.alertTime = alertTime;
    }

    public void updateInstitution(Institution institution) {
        this.institution = institution;
    }

}