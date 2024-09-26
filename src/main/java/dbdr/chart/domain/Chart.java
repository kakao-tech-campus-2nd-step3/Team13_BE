package dbdr.domain.chart;


import dbdr.domain.core.entity.BaseEntity;
import dbdr.domain.recipient.entity.Recipient;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "chart")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE chart SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Chart extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @Column(nullable = false, length = 500)
    private String conditionDisease;

    @OneToOne
    @JoinColumn(name = "body_management_id")
    private BodyManagement bodyManagement;

    @OneToOne
    @JoinColumn(name = "nursing_management_id")
    private NursingManagement nursingManagement;

    @OneToOne
    @JoinColumn(name = "recovery_training_id")
    private RecoveryTraining recoveryTraining;

    @OneToOne
    @JoinColumn(name = "cognitive_management_id")
    private CognitiveManagement cognitiveManagement;

    public void update(Chart chart) {
        this.conditionDisease = chart.getConditionDisease();
        this.bodyManagement = chart.getBodyManagement();
        this.nursingManagement = chart.getNursingManagement();
        this.recoveryTraining = chart.getRecoveryTraining();
        this.cognitiveManagement = chart.getCognitiveManagement();
    }
}
