package dbdr.domain.chart.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "recovery_training")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE recovery_training SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class RecoveryTraining extends BaseEntity {
    @Comment("회복 프로그램")
    @Column(length = 255)
    private String recoveryProgram; // 회복 프로그램 이름

    @Comment("회복 훈련 완료 여부")
    private boolean recoveryTraining; // 회복훈련 완료 여부

    @Comment("인지훈련 제공 여부")
    private boolean isCognitiveTrainingProvided; // 인지훈련 제공 여부

    @Comment("물리치료 제공 여부")
    private boolean isPhysicalTherapyProvided; // 물리치료 제공 여부

    @Comment("회복 훈련 특이사항")
    @Column(columnDefinition = "TEXT")
    private String recoveryNote; // 회복훈련 특이사항

}
