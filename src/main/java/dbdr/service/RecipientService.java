package dbdr.service;

import dbdr.domain.Recipient;
import dbdr.dto.request.RecipientRequestDTO;
import dbdr.dto.response.RecipientResponseDTO;
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
    public List<RecipientResponseDTO> getAllRecipients() {
        return recipientRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientById(Long id) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄대상자를 찾을 수 없습니다."));
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO createRecipient(RecipientRequestDTO recipientRequestDTO) {
        Recipient recipient = new Recipient(
                recipientRequestDTO.getName(),
                recipientRequestDTO.getBirth(),
                recipientRequestDTO.getGender(),
                recipientRequestDTO.getCareLevel(),
                recipientRequestDTO.getCareNumber(),
                recipientRequestDTO.getStartDate(),
                recipientRequestDTO.getInstitution(),
                recipientRequestDTO.getInstitutionNumber(),
                careworkerRepository.findById(recipientRequestDTO.getCareworkerId())
                        .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다."))
        );
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO updateRecipient(Long id, RecipientRequestDTO recipientRequestDTO) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄대상자를 찾을 수 없습니다."));

        recipient.updateRecipient(recipientRequestDTO);
        recipientRepository.save(recipient);

        return toResponseDTO(recipient);
    }

    @Transactional
    public void deleteRecipient(Long id) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("돌봄대상자를 찾을 수 없습니다."));
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    private RecipientResponseDTO toResponseDTO(Recipient recipient) {
        return new RecipientResponseDTO(
                recipient.getId(),
                recipient.getName(),
                recipient.getBirth(),
                recipient.getGender(),
                recipient.getCareLevel(),
                recipient.getCareNumber(),
                recipient.getStartDate(),
                recipient.getInstitution(),
                recipient.getInstitutionNumber(),
                recipient.getCareworker() != null ? recipient.getCareworker().getId() : null
        );
    }
}

