package dbdr.chart.domain;

import dbdr.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "body_management")
@Getter
@SQLDelete(sql = "UPDATE body_management SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class BodyManagement extends BaseEntity {
    @Embedded
    private PhysicalClear physicalClear; // 세면 및 목욕 체크박스

    @Embedded
    private PhysicalMeal physicalMeal; // 식사 종류와 섭취량

    @Comment("화장실 횟수")
    private int physicalRestroom; // 화장실 횟수

    @Embedded
    private PhysicalWalk physicalWalk; // 산책 or 외출 동행

    @Comment("특이사항")
    @Column(columnDefinition = "TEXT")
    private String physicalNote; // 특이사항 입력
}
