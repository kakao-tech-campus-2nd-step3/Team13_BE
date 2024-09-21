package dbdr.domain.chart;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalClear {
    // 세면 유무
    private boolean wash;

    // 목욕 유무
    private boolean bath;
}