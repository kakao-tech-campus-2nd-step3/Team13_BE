package dbdr.domain.careworker.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.institution.entity.Institution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import java.util.EnumSet;
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

    @Column(nullable = false)
    private int workDays; // 비트 플래그로 요일 저장

    @Column(nullable = true)
    private String lineUserId;

    @Column(nullable = false)
    private LocalTime alertTime = LocalTime.of(17, 0); // 오후 5시로 초기화

    @Column(nullable = false)
    private boolean smsSubscription = false;

    @Column(nullable = false)
    private boolean lineSubscription = false;

    @Column(unique = true)
    private String email;

    @Builder
    public Careworker(String loginPassword, String phone, String name, Institution institution,
                      String email) {
        this.loginPassword = loginPassword;
        this.phone = phone;
        this.name = name;
        this.institution = institution;
        this.email = email;
        this.alertTime = LocalTime.of(17, 0); // 오후 5시로 초기화
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
        this.workDays = 0; // 초기화하여 기존 값을 제거합니다.
        for (DayOfWeek day : workingDays) {
            this.workDays |= (1 << (day.getValue() - 1)); // 각 요일을 비트 플래그로 추가합니다.
        }
    }

    public Set<DayOfWeek> getWorkingDays() {
        Set<DayOfWeek> workingDays = EnumSet.noneOf(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            if ((this.workDays & (1 << (day.getValue() - 1))) != 0) {
                workingDays.add(day);
            }
        }
        return workingDays;
    }
    public void updateAlertTime(LocalTime alertTime) {
        this.alertTime = alertTime;
    }

    // 요일 설정 및 조회 메서드
    public void addWorkDay(DayOfWeek day) {
        this.workDays |= day.getValue();
    }

    // 다음 근무일 찾기
    public DayOfWeek getNextWorkingDay(DayOfWeek currentDay) {
        for (int i = 1; i <= 7; i++) { // 최대 7일을 순환하여 다음 근무일 찾기
            DayOfWeek nextDay = currentDay.plus(i);
            if (isWorkingOn(nextDay)) {
                return nextDay;
            }
        }
        return null;
    }

    // 근무일인지 확인하기
    public boolean isWorkingOn(DayOfWeek day) {
        return (this.workDays & (1 << (day.getValue() - 1))) != 0;
    }

    public void updateInstitution(Institution institution) {
      this.institution = institution;
    }

    public void updateSubscriptions(boolean smsSubscription, boolean lineSubscription) {
        this.smsSubscription = smsSubscription;
        this.lineSubscription = lineSubscription;
    }
}
