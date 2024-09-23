package dbdr.domain.chart;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalWalk {
    private boolean has_walked; // 산책
    private boolean has_companion; // 외출 동행
}
