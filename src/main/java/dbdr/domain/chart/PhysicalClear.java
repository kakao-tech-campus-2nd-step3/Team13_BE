package dbdr.domain.chart;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class PhysicalClear {
    // 세면 유무
    private boolean wash;

    // 목욕 유무
    private boolean bath;
}