package dbdr.domain.chart.repository;

import dbdr.domain.chart.entity.Chart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<Chart, Long> {
    List<Chart> findAllByRecipientId(Long recipientId);
}
