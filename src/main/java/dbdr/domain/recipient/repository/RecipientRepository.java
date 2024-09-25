package dbdr.domain.recipient.repository;

import dbdr.domain.recipient.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
}
