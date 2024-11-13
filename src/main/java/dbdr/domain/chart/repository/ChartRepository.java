package dbdr.domain.chart.repository;

import dbdr.domain.chart.entity.Chart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<Chart, Long> {
	List<Chart> findAllByRecipientId(Long recipientId);

	Optional<Chart> findByRecipientIdAndCreatedAtBetween(Long recipientId, LocalDateTime startDateTime, LocalDateTime endDateTime);}
