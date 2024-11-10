package dbdr.domain.admin.entity;

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
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE admins SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Admin extends BaseEntity {

    @Column(unique = true)
    private String loginId;

    private String loginPassword;

    @Builder
    public Admin(String loginId, String loginPassword) {
        this.loginId = loginId;
        this.loginPassword = loginPassword;
    }

    public void changePassword(String password) {
        this.loginPassword = password;
    }

}
