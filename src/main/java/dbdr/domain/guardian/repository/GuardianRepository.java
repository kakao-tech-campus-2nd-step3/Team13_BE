package dbdr.domain.guardian.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dbdr.domain.guardian.entity.Guardian;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    boolean existsByPhone(String phone);

    Guardian findByLineUserId(String userId);

    List<Guardian> findByAlertTime(LocalTime currentTime);

	Guardian findByPhone(String phone);
}
