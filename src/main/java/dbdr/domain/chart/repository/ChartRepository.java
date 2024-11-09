package dbdr.domain.chart.repository;

import dbdr.domain.chart.entity.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<Chart, Long> {
    Page<Chart> findAllByRecipientId(Long recipientId, Pageable pageable);

}
