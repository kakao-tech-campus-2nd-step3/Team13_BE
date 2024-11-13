package dbdr.domain.core.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dbdr.domain.core.alarm.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
