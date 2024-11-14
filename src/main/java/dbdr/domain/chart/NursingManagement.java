package dbdr.domain.chart;

import dbdr.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "nursing_management")
@SQLDelete(sql = "UPDATE nursing_management SET is_active = false WHERE id = ?")
public class NursingManagement extends BaseEntity {
    @Embedded
    private HealthBloodPressure healthBloodPressure; // 혈압 임베디드 타입

    private String healthTemperature; // 체온

    @Column(length = 1000)
    private String healthNote; // 건강 및 간호관리 특이사항

}
