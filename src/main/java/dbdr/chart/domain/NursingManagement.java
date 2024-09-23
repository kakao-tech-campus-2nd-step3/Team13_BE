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
@Table(name = "nursing_management")
@Getter
@SQLDelete(sql = "UPDATE nursing_management SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class NursingManagement extends BaseEntity {
    @Embedded
    private HealthBloodPressure healthBloodPressure; // 혈압 임베디드 타입

    @Comment("체온")
    private String healthTemperature; // 체온

    @Comment("특이사항")
    @Column(length = 1000)
    private String healthNote; // 건강 및 간호관리 특이사항

}
