package dbdr.domain.core.messaging.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dbdr.domain.core.messaging.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	Optional<Alarm> findByPhone(String phone);
}
