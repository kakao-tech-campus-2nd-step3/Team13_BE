package dbdr.domain.chart.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "nursing_management")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE nursing_management SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class NursingManagement extends BaseEntity {
    @Embedded
    public HealthBloodPressure healthBloodPressure; // 혈압 임베디드 타입

    @Comment("체온")
    private String healthTemperature; // 체온

    @Comment("건강 관리 제공 여부")
    private boolean healthCareProvided; // 건강 관리 제공 여부

    @Comment("간호 관리 제공 여부")
    private boolean nursingCareProvided; // 간호 관리 제공 여부

    @Comment("응급 관리 제공 여부")
    private boolean emergencyCareProvided; //  응급 관리 제공 여부

    @Comment("특이사항")
    @Column(columnDefinition = "TEXT")
    private String healthNote; // 건강 및 간호관리 특이사항

}
