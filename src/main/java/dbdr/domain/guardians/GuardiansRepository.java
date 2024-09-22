package dbdr.domain.guardians;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardiansRepository extends JpaRepository<Guardians, Long> {

    boolean existsByPhone(String phone);
}
