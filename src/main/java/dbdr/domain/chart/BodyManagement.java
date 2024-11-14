package dbdr.domain.chart;

import dbdr.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "body_management")
@SQLDelete(sql = "UPDATE body_management SET is_active = false WHERE id = ?")
public class BodyManagement extends BaseEntity {
    @Embedded
    private PhysicalClear physicalClear; // 세면 및 목욕 체크박스

    @Embedded
    private PhysicalMeal physicalMeal; // 식사 종류와 섭취량

    @Column(length = 50)
    private int physicalRestroom; // 화장실 횟수

    @Column(length = 255)
    private String physicalWalk; // 산책 or 외출 동행

    @Column(length = 1000)
    private String physicalNote; // 특이사항 입력
}
