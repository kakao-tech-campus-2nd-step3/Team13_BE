package dbdr.domain.institution.repository;

import dbdr.domain.institution.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    boolean existsByInstitutionNumber(Long institutionNumber);
    boolean existsByInstitutionName(String institutionName);
    boolean existsByLoginId(String loginId);

    Institution findByInstitutionNumber(Long institutionNumber);
    Institution findByLoginId(String loginId);
}
