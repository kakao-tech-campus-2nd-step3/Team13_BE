package dbdr.domain.chart.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE cognitive_management SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class CognitiveManagement extends BaseEntity {
    @Comment("의사소통 도움 여부")
    private boolean cognitiveHelp; // 의사소통 도움 여부

    @Comment("말벗 및 격려 여부")
    private boolean companionshipProvided; // 말벗 및 격려 여부

    @Comment("인지 관리 특이사항")
    @Column(columnDefinition = "TEXT")
    private String cognitiveNote; // 인지 관리 특이사항
}
