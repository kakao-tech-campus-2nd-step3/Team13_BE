package dbdr.domain.chart.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthBloodPressure {

    @Comment("혈압 최고")
    private String systolic;

    @Comment("혈압 최저")
    private String diastolic;
}
