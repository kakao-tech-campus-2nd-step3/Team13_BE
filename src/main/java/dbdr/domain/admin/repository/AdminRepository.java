package dbdr.domain.admin.repository;

import dbdr.domain.admin.entity.Admin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByLoginId(String loginId);
    List<Admin> findAll();

}
