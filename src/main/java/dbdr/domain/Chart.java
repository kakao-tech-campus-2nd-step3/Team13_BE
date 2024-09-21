package dbdr.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "chart")
@SQLDelete(sql = "UPDATE chart SET is_deleted = false WHERE id = ?")
@SQLRestriction("isDeleted = true")
public class Chart extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient", nullable = false)
    private Recipient recipient;
}
