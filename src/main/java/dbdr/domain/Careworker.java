package dbdr.domain;

import dbdr.dto.request.CareworkerRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "careworker")
@SQLDelete(sql = "UPDATE careworker SET is_active = false WHERE id = ?")
@SQLRestriction("is_active= true")
public class Careworker extends BaseEntity {

    @Column(nullable = false)
    private Long institutionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;


    public Careworker(Long institutionId, String name, String email, String phone) {
        this.institutionId = institutionId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void updateCareworker(CareworkerRequestDTO careworkerDTO) {
        //this.institutionId = careworkerDTO.getInstitutionId();
        this.name = careworkerDTO.getName();
        this.email = careworkerDTO.getEmail();
        this.phone = careworkerDTO.getPhone();
    }
}