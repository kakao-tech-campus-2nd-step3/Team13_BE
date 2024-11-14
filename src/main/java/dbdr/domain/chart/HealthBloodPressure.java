package dbdr.domain.chart;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HealthBloodPressure {

    private int systolic;  // 혈압 최고
    private int diastolic; // 혈압 최저
}