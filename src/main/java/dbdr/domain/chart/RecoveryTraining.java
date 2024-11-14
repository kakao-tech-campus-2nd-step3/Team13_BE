package dbdr.domain.chart;

import dbdr.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "recovery_training")
@SQLDelete(sql = "UPDATE recovery_training SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class RecoveryTraining extends BaseEntity {
    @Column(length = 255)
    private String recoveryProgram; // 회복 프로그램 이름

    private boolean recoveryTraining; // 회복훈련 완료 여부

    @Column(length = 1000)
    private String recoveryNote; // 회복훈련 특이사항

}
