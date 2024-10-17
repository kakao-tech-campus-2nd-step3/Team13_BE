package dbdr.domain.recipient.service;

import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final CareworkerRepository careworkerRepository;

    @Transactional(readOnly = true)
    public List<RecipientResponseDTO> getAllRecipients() {
        return recipientRepository.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientById(Long id) {
        Recipient recipient = findRecipientById(id);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO createRecipient(RecipientRequestDTO recipientRequestDTO) {
        ensureUniqueCareNumber(recipientRequestDTO.getCareNumber());
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
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND))
        );
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO updateRecipient(Long id, RecipientRequestDTO recipientRequestDTO) {
        ensureUniqueCareNumber(recipientRequestDTO.getCareNumber());

        Recipient recipient = findRecipientById(id);

        recipient.updateRecipient(recipientRequestDTO);
        recipientRepository.save(recipient);

        return toResponseDTO(recipient);
    }

    @Transactional
    public void deleteRecipient(Long id) {
        Recipient recipient = findRecipientById(id);
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    private Recipient findRecipientById(Long id) {
        return recipientRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
    }

    private void ensureUniqueCareNumber(String careNumber) {
        if (recipientRepository.existsByCareNumber(careNumber)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_CARE_NUMBER);
        }
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

