package dbdr.domain.chart;


import dbdr.domain.BaseEntity;
import dbdr.domain.Recipient;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "chart")
@SQLDelete(sql = "UPDATE chart SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Chart extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @OneToOne
    @JoinColumn(name = "body_management_id")
    private BodyManagement bodyManagement;

    @OneToOne
    @JoinColumn(name = "nursing_management_id")
    private NursingManagement nursingManagement;

    @OneToOne
    @JoinColumn(name = "recovery_training_id")
    private RecoveryTraining recoveryTraining;
}
