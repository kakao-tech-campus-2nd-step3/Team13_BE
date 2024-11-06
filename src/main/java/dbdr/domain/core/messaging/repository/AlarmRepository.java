package dbdr.domain.core.messaging.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dbdr.domain.core.messaging.entity.Alarm;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	Optional<Alarm> findByPhone(String phone);

	Optional<Alarm> findByPhoneAndAlertTime(String phone, LocalDateTime alertTime);
}
