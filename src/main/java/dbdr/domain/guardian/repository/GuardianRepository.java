package dbdr.domain.guardian.repository;

import dbdr.domain.guardian.entity.Guardian;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    boolean existsByPhone(String phone);

    Optional<Guardian> findByPhone(String phone);
}