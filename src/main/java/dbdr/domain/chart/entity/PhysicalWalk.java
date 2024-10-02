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
public class PhysicalWalk {
    @Comment("산책 여부")
    private boolean hasWalked;

    @Comment("외출 동행")
    private boolean hasCompanion;
}
