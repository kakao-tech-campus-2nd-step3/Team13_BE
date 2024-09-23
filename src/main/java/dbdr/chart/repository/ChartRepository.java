package dbdr.chart.repository;

import dbdr.chart.domain.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<Chart, Long> {
    Page<Chart> findAllByRecipientId(Long recipientId, Pageable pageable);
}
