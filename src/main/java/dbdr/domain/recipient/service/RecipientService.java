package dbdr.domain.recipient.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final InstitutionService institutionService;
    private final CareworkerService careworkerService;

    @Transactional(readOnly = true)
    public List<RecipientResponseDTO> getAllRecipients() {
        return recipientRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientById(Long recipientId) {
        Recipient recipient = findRecipientById(recipientId);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO createRecipient(RecipientRequestDTO recipientRequestDTO) {
        Institution institution = institutionService.getInstitutionById(recipientRequestDTO.getInstitutionId());
        Careworker careworker = careworkerService.getCareworkerById(recipientRequestDTO.getCareworkerId());
        ensureUniqueCareNumber(recipientRequestDTO.getCareNumber());

        Recipient recipient = new Recipient(
                recipientRequestDTO.getName(),
                recipientRequestDTO.getBirth(),
                recipientRequestDTO.getGender(),
                recipientRequestDTO.getCareLevel(),
                recipientRequestDTO.getCareNumber(),
                recipientRequestDTO.getStartDate(),
                institution,
                institution.getInstitutionNumber(),
                careworker
        );
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    @Transactional
    public RecipientResponseDTO updateRecipient(Long recipientId, RecipientRequestDTO recipientRequestDTO,
                                                Long institutionId, Long careworkerId) {
        Recipient recipient = findRecipientById(recipientId);

        // 권한 검증
        validateAccessPermission(recipient, institutionId, careworkerId);

        recipient.updateRecipient(recipientRequestDTO);
        return toResponseDTO(recipient);
    }

    @Transactional
    public void deleteRecipient(Long recipientId, Long institutionId, Long careworkerId) {
        Recipient recipient = findRecipientById(recipientId);

        // 권한 검증
        validateAccessPermission(recipient, institutionId, careworkerId);

        recipient.deactivate();
        recipientRepository.delete(recipient);
    }


    private void validateAccessPermission(Recipient recipient, Long institutionId, Long careworkerId) {
        if (institutionId != null) {
            // 요양원 관리자인 경우, institutionId와 돌봄대상자의 요양원 ID가 일치하는지 확인
            if (!recipient.getInstitution().getId().equals(institutionId)) {
                throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
            }
        } else if (careworkerId != null) {
            // 요양보호사인 경우, 본인의 돌봄대상자인지 확인
            if (recipient.getCareworker() == null || !recipient.getCareworker().getId().equals(careworkerId)) {
                throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
            }
        } else {
            // 권한 정보가 없는 경우 접근 제한
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
    }

    public Recipient findRecipientById(Long recipientId) {
        return recipientRepository.findById(recipientId)
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
                recipient.getInstitution().getInstitutionName(),
                recipient.getInstitution().getInstitutionNumber(),
                recipient.getInstitution().getId(),
                recipient.getCareworker() != null ? recipient.getCareworker().getId() : null
        );
    }
}

