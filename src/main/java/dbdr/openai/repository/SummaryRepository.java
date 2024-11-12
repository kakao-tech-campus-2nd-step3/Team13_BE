package dbdr.openai.repository;

import dbdr.openai.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Summary findByChartId(Long chartId);
}
