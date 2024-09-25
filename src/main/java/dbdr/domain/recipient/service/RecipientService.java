package dbdr.domain.recipient.service;

import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.exception.IdNotFoundException;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.exception.NotUniqueException;
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
        careNumberExists(recipientRequestDTO.getCareNumber());
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
                .orElseThrow(() -> new IdNotFoundException("요양보호사를 찾을 수 없습니다."))
        );
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO updateRecipient(Long id, RecipientRequestDTO recipientRequestDTO) {
        careNumberExists(recipientRequestDTO.getCareNumber());

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
            .orElseThrow(() -> new IdNotFoundException("돌봄대상자를 찾을 수 없습니다."));
    }

    private void careNumberExists(String careNumber) {
        if (recipientRepository.existsByCareNumber(careNumber)) {
            throw new NotUniqueException("케어번호가 존재합니다.");
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

