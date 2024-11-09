package dbdr.domain.careworker.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.institution.entity.Institution;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "careworkers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE careworkers SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Careworker extends BaseEntity {

    @Column(unique = true)
    private String loginId;

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

    @Column(nullable = false)
    private int workDays; // 비트 플래그로 요일 저장

    @Column(nullable = true)
    private String lineUserId;

    @Column(nullable = true)
    private LocalTime alertTime;

    @Column(unique = true)
    private String email;

    @Builder
    public Careworker(Institution institution, String name, String email, String phone) {
        this.institution = institution;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.alertTime = LocalTime.of(17, 0);
    }

    public void updateCareworker(CareworkerRequestDTO careworkerDTO) {
        this.name = careworkerDTO.getName();
        this.email = careworkerDTO.getEmail();
        this.phone = careworkerDTO.getPhone();
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

    // 근무일 추가하기
    public void addWorkDay(DayOfWeek day) {
        this.workDays |= day.getValue();
    }

    // 가장 가까운 다음 근무일 찾기
    public DayOfWeek getNextWorkingDay(DayOfWeek currentDay) {
        for (int i = 1; i <= 7; i++) {
            DayOfWeek nextDay = currentDay.plus(i);
            if (isWorkingOn(nextDay)) {
                return nextDay;
            }
        }
        return null;
    }

    // 해당 요일이 근무일인지 확인하기
    public boolean isWorkingOn(DayOfWeek day) {
        return (this.workDays & (1 << (day.getValue() - 1))) != 0;
    }

    public void updateInstitution(Institution institution) {
        this.institution = institution;
    }

}
