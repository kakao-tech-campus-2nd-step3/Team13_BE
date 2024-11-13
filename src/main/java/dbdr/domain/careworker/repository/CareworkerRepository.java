package dbdr.domain.careworker.repository;

import dbdr.domain.careworker.entity.Careworker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CareworkerRepository extends JpaRepository<Careworker, Long> {

    List<Careworker> findAllByInstitutionId(Long institutionId);

    Optional<Careworker> findByLineUserId(String userId);

    List<Careworker> findByAlertTime(LocalTime currentTime);

    Optional<Careworker> findByPhone(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

	boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmailAndIdNot(String phone, Long id);
}
