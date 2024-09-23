package dbdr.repository;

import dbdr.domain.Careworker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareworkerRepository extends JpaRepository<Careworker, Long> {

    List<Careworker> findByInstitutionId(Long institutionId);
}
