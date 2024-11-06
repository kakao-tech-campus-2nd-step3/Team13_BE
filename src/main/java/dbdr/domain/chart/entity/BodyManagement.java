package dbdr.domain.chart.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "body_management")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE body_management SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class BodyManagement extends BaseEntity {
    @Embedded
    public PhysicalMeal physicalMeal; // 식사 종류와 섭취량

    @Embedded
    public PhysicalClear physicalClear; // 세면 및 목욕 체크박스

    @Comment("화장실 횟수")
    private int physicalRestroom; // 화장실 횟수

    @Comment("특이사항")
    @Column(columnDefinition = "TEXT")
    private String physicalNote; // 특이사항 입력

    @Comment("체위 변경 유무")
    private boolean isPositionChangeRequired; // 체위 변경 유무

    @Comment("이동 도움 유무")
    private boolean isMobilityAssistance; // 이동 도움 유무

    @Comment("특이사항")
    private boolean hasWalked;// 산책

}
