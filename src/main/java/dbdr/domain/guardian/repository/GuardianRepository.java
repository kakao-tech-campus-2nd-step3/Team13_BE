package dbdr.domain.guardian.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dbdr.domain.guardian.entity.Guardian;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<Guardian> findByLineUserId(String userId);

    List<Guardian> findByAlertTime(LocalTime currentTime);

	Optional<Guardian> findByPhone(String phone);
}
