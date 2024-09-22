package dbdr.service;

import dbdr.domain.Recipient;
import dbdr.dto.RecipientDTO;
import dbdr.repository.RecipientRepository;
import dbdr.repository.CareworkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final CareworkerRepository careworkerRepository;

    public RecipientService(RecipientRepository recipientRepository, CareworkerRepository careworkerRepository) {
        this.recipientRepository = recipientRepository;
        this.careworkerRepository = careworkerRepository;
    }

    @Transactional(readOnly = true)
    public List<RecipientDTO> getAllRecipients() {
        return recipientRepository.findAll().stream()
                .filter(Recipient::isActive)  // 활성 상태만 필터링
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public RecipientDTO getRecipientById(Long id) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));
        return toDTO(recipient);
    }

    @Transactional
    public RecipientDTO createRecipient(RecipientDTO recipientDTO) {
        Recipient recipient = new Recipient(
                recipientDTO.getName(),
                recipientDTO.getBirth(),
                recipientDTO.getGender(),
                recipientDTO.getCareLevel(),
                recipientDTO.getCareNumber(),
                recipientDTO.getStartDate(),
                recipientDTO.getInstitution(),
                recipientDTO.getInstitutionNumber(),
                careworkerRepository.findById(recipientDTO.getCareworkerId())
                        .orElseThrow(() -> new IllegalArgumentException("Careworker not found"))
        );
        recipientRepository.save(recipient);
        return toDTO(recipient);
    }

    @Transactional
    public RecipientDTO updateRecipient(Long id, RecipientDTO recipientDTO) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        recipient.updateRecipient(recipientDTO.getName(), recipientDTO.getBirth(), recipientDTO.getGender(),
                recipientDTO.getCareLevel(), recipientDTO.getCareNumber(), recipientDTO.getStartDate(),
                recipientDTO.getInstitution(), recipientDTO.getInstitutionNumber(),
                careworkerRepository.findById(recipientDTO.getCareworkerId())
                        .orElseThrow(() -> new IllegalArgumentException("Careworker not found")));

        recipientRepository.save(recipient);
        return toDTO(recipient);
    }

    @Transactional
    public void deleteRecipient(Long id) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    private RecipientDTO toDTO(Recipient recipient) {
        return new RecipientDTO(
                recipient.getId(),
                recipient.getName(),
                recipient.getBirth(),
                recipient.getGender(),
                recipient.getCareLevel(),
                recipient.getCareNumber(),
                recipient.getStartDate(),
                recipient.getInstitution(),
                recipient.getInstitutionNumber(),
                recipient.getCareworker() != null ? recipient.getCareworker().getId() : null,
                recipient.getCreatedAt(),
                recipient.getUpdateAt(),
                recipient.isActive()
        );
    }
}