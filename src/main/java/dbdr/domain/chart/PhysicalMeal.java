package dbdr.domain.chart;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class PhysicalMeal {
    // 식사 종류
    private String mealType;

    // 섭취량
    private String intakeAmount;
}
