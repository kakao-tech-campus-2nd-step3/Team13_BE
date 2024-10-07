package dbdr.domain.institution.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "institutions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE institutions SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Institution extends BaseEntity {
    @Column(unique = true)
    private String loginId;

    private String loginPassword;

    @Column(nullable = false, unique = true)
    private Long institutionNumber;

    @Column(nullable = false, length = 100)
    private String institutionName;

    @Builder
    public Institution(Long institutionNumber, String institutionName) {
        this.institutionNumber = institutionNumber;
        this.institutionName = institutionName;
    }

    public void updateInstitution(Long institutionNumber, String institutionName) {
        this.institutionNumber = institutionNumber;
        this.institutionName = institutionName;
    }
}
