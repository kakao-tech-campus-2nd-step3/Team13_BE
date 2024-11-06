package dbdr.domain.careworker.repository;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.institution.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CareworkerRepository extends JpaRepository<Careworker, Long> {

    List<Careworker> findByInstitutionId(Long institutionId);

    Optional<Careworker> findByLineUserId(String userId);

    List<Careworker> findByAlertTime(LocalTime currentTime);

    Optional<Careworker> findByPhone(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
