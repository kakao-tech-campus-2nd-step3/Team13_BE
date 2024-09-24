package dbdr.domain.guardian.entity;

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
public class Guardian extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    public Guardian(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    @Builder
    public void updateGuardian(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }
}