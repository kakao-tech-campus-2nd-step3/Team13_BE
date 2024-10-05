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
public class PhysicalMeal {
    @Comment("식사 종류")
    private String mealType;

    @Comment("섭취량")
    private String intakeAmount;
}
