package dbdr.domain.guardians;

import dbdr.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(name = "guardians")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guardians extends BaseEntity {

    @Comment("보호자 전화번호")
    @Column(nullable = false, unique = true)
    private String phone;
    @Comment("보호자 성명")
    @Column(nullable = false)
    private String name;

    @Builder
    public Guardians(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    @Builder
    public void updateGuardian(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }
}
