package dbdr.chart.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalWalk {
    @Comment("산책 여부")
    private boolean hasWalked;

    @Comment("외출 동행")
    private boolean hasCompanion;
}
