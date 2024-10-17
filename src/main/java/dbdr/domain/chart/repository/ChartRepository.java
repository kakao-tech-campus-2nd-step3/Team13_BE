package dbdr.domain.chart.repository;

import dbdr.domain.chart.entity.Chart;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChartRepository extends JpaRepository<Chart, Long> {
    Page<Chart> findAllByRecipientId(Long recipientId, Pageable pageable);

    @Query("SELECT c FROM Chart c WHERE c.recipient.id = :recipientId AND c.createdAt >= :sevenDaysAgo AND c.createdAt <= :currentDate")
    List<Chart> findAllWithinSevenDaysByRecipientId(@Param("recipientId") Long recipientId, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, @Param("currentDate") LocalDateTime currentDate);
}
