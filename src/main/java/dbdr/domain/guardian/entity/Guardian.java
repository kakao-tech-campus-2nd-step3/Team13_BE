package dbdr.domain.guardian.entity;

import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import dbdr.domain.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    @Column(unique = true)
    private String loginId;

    @Column(nullable = false)
    private String loginPassword;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "010\\d{8}")
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    public Guardian(String phone, String name, String loginPassword) {
        this.phone = phone;
        this.name = name;
        this.loginPassword = loginPassword;
    }

    @Builder
    public void updateGuardian(String phone, String name, String loginPassword) {
        this.phone = phone;
        this.name = name;
        this.loginPassword = loginPassword;
    }
}
