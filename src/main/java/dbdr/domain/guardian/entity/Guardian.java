package dbdr.domain.guardian.entity;

import dbdr.domain.institution.entity.Institution;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "guardians")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE guardians SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Guardian extends BaseEntity {
    private String loginPassword;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "010\\d{8}")
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true)
    private String lineUserId;

    @Column(nullable = false)
    private LocalTime alertTime = LocalTime.of(9, 0); // 오전 9시로 초기화

    @Column(nullable = false)
    private boolean smsSubscription = false;

    @Column(nullable = false)
    private boolean lineSubscription = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Builder
    public Guardian(String loginPassword, String phone, String name, Institution institution) {
        this.phone = phone;
        this.name = name;
        this.institution = institution;
        this.loginPassword = loginPassword;
        this.alertTime = LocalTime.of(18, 0); // 오후 6시로 초기화
    }

    public void updateGuardian(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public void updateAlertTime(LocalTime alertTime) {
        this.alertTime = alertTime;
    }

    public void updateLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public void updateSubscriptions(boolean smsSubscription, boolean lineSubscription) {
        this.smsSubscription = smsSubscription;
        this.lineSubscription = lineSubscription;
    }
}
