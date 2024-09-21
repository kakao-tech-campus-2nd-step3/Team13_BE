package dbdr.domain.chart;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "nursing_management")
@SQLDelete(sql = "UPDATE nursing_management SET is_deleted = false WHERE id = ?")
public class NursingManagement {
    @Embedded
    private HealthBloodPressure healthBloodPressure; // 혈압 임베디드 타입

    @Column(name = "health_temperature")
    private String healthTemperature; // 체온

    @Column(name = "health_note", length = 1000)
    private String healthNote; // 건강 및 간호관리 특이사항

}
