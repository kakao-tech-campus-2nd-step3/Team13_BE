package dbdr.domain.guardian.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import dbdr.domain.guardian.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    boolean existsByPhone(String phone);

    boolean existsByName(String name);

    Optional<Guardian> findByName(String name);

    Optional<Guardian> findByLineUserId(String userId);

    List<Guardian> findByAlertTime(LocalTime currentTime);

	Optional<Guardian> findByPhone(String phone);
}
