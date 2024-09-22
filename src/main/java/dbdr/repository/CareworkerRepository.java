package dbdr.repository;

import dbdr.domain.Careworker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareworkerRepository extends JpaRepository<Careworker, Long> {

}