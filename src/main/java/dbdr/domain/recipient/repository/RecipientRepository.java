package dbdr.domain.recipient.repository;

import dbdr.domain.recipient.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    boolean existsByCareNumber(String careNumber);

    List<Recipient> findByCareworkerId(Long careworkerId);

    List<Recipient> findByInstitutionId(Long institutionId);

    List<Recipient> findAllByGuardianId(Long guardianId);

    Optional<Recipient> findByIdAndCareworkerId(Long recipientId, Long careworkerId);

    Optional<Recipient> findByIdAndInstitutionId(Long recipientId, Long institutionId);

    Optional<Recipient> findByIdAndGuardianId(Long recipientId, Long guardianId);
}