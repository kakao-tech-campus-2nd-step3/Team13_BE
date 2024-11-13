package dbdr.domain.recipient.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.chart.service.ChartService;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.dto.request.RecipientUpdateCareworkerRequest;
import dbdr.domain.recipient.dto.request.RecipientUpdateInstitutionRequest;
import dbdr.domain.recipient.dto.response.RecipientResponse;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final CareworkerService careworkerService;
    private final InstitutionService institutionService;
    private final GuardianService guardianService;
    private final ChartRepository chartRepository;

    // 전체 돌봄대상자 목록 조회 (관리자용)
    public List<RecipientResponse> getAllRecipients() {
        return recipientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 특정 돌봄대상자 조회 (관리자용)
    @Transactional(readOnly = true)
    public RecipientResponse getRecipientById(Long recipientId) {
        Recipient recipient = findRecipientById(recipientId);
        return toResponse(recipient);
    }

    //관리자용
    @Transactional
    public RecipientResponse createRecipient(RecipientRequest recipientDTO) {
        ensureUniqueCareNumber(recipientDTO.getCareNumber());
        Careworker careworker = careworkerService.getCareworkerById(recipientDTO.getCareworkerId());
        Institution institution = institutionService.getInstitutionById(recipientDTO.getInstitutionId());

        //  Careworker가 해당 Institution에 속하는지 확인
        if (!careworker.getInstitution().getId().equals(institution.getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        Guardian guardian = guardianService.findGuardianById(recipientDTO.getGuardianId());
        if (!guardian.getInstitution().getId().equals(institution.getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        Recipient recipient = new Recipient(recipientDTO, institution, careworker, guardian);
        recipientRepository.save(recipient);
        return toResponse(recipient);
    }

    //관리자용
    @Transactional
    public RecipientResponse updateRecipientForAdmin(Long recipientId, RecipientRequest recipientDTO) {
        Recipient recipient = findRecipientById(recipientId);
        Institution institution = institutionService.getInstitutionById(recipientDTO.getInstitutionId());
        if (institution == null) {
            throw new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND);
        }

        Careworker careworker = careworkerService.getCareworkerById(recipientDTO.getCareworkerId());
        if (careworker == null) {
            throw new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND);
        }

        // Careworker가 해당 Institution에 속하는지 확인
        if (!careworker.getInstitution().getId().equals(institution.getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        Guardian guardian = guardianService.findGuardianById(recipientDTO.getGuardianId());
        // guardian가 해당 Institution에 속하는지 확인
        if (!guardian.getInstitution().getId().equals(institution.getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        //관리자는 요양원, 요양보호사, 보호자 업데이트 가능
        recipient.updateRecipient(recipientDTO);
        recipient.updateRecipientForAdmin(recipientDTO, institution, careworker, guardian);

        return toResponse(recipient);
    }


    // 돌봄대상자 삭제 (관리자용)
    @Transactional
    public void deleteRecipient(Long recipientId) {
        Recipient recipient = findRecipientById(recipientId);
        recipientRepository.delete(recipient);
    }

    //보호자용
    @Transactional(readOnly = true)
    public List<RecipientResponse> getAllRecipientsForGuardian(Long guardianId) {
        return recipientRepository.findAllByGuardianId(guardianId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    //보호자용
    @Transactional(readOnly = true)
    public RecipientResponse getRecipientForGuardian(Long guardianId, Long recipientId) {
        return recipientRepository.findByIdAndGuardianId(recipientId, guardianId)
                .map(this::toResponse)
                .orElseThrow(() -> new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
    }

    //전체 돌봄대상자 목록 조회 (요양보호사용)
    @Transactional(readOnly = true)
    public List<RecipientResponse> getRecipientsByCareworker(Long careworkerId) {
        return recipientRepository.findByCareworkerId(careworkerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    //전체 돌봄대상자 목록 조회 (요양원용)
    public List<RecipientResponse> getRecipientsByInstitution(Long institutionId) {
        return recipientRepository.findByInstitutionId(institutionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //요양보호사가 담당하는 특정 돌봄대상자 정보 조회
    @Transactional(readOnly = true)
    public RecipientResponse getRecipientByCareworker(Long recipientId, Long careworkerId) {
        Recipient recipient = findRecipientByIdAndCareworker(recipientId, careworkerId);
        return toResponse(recipient);
    }

    //요양원이 관리하는 특정 돌봄대상자 정보 조회
    @Transactional(readOnly = true)
    public RecipientResponse getRecipientByInstitution(Long recipientId, Long institutionId) {
        Recipient recipient = findRecipientByIdAndInstitution(recipientId, institutionId);
        return toResponse(recipient);
    }

    //요양보호사가 새로운 돌봄대상자를 추가
    @Transactional
    public RecipientResponse createRecipientForCareworker(RecipientRequest recipientDTO, Long careworkerId) {
        ensureUniqueCareNumber(recipientDTO.getCareNumber());
        Careworker careworker = careworkerService.getCareworkerById(careworkerId);

        if (!careworker.getInstitution().getId().equals(recipientDTO.getInstitutionId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        Guardian guardian = guardianService.findGuardianById(recipientDTO.getGuardianId());

        Recipient recipient = new Recipient(recipientDTO, careworker, guardian);
        recipientRepository.save(recipient);
        return toResponse(recipient);
    }

    //요양원이 새로운 돌봄대상자(요양보호사 배정 필수)를 추가
    @Transactional
    public RecipientResponse createRecipientForInstitution(RecipientRequest recipientDTO, Long institutionId) {
        ensureUniqueCareNumber(recipientDTO.getCareNumber());
        Institution institution = institutionService.getInstitutionById(institutionId);
        Careworker careworker = careworkerService.getCareworkerById(recipientDTO.getCareworkerId());

        if (!careworker.getInstitution().getId().equals(institution.getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        Guardian guardian = guardianService.findGuardianById(recipientDTO.getGuardianId());

        if (!guardian.getInstitution().getId().equals(institution.getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        Recipient recipient = new Recipient(recipientDTO, institution, careworker, guardian);
        recipientRepository.save(recipient);
        return toResponse(recipient);
    }

    //요양보호사가 담당하는 돌봄대상자 정보 수정
    @Transactional
    public RecipientResponse updateRecipientForCareworker(Long recipientId, RecipientUpdateCareworkerRequest recipientDTO, Long careworkerId) {
        Careworker careworker = careworkerService.getCareworkerById(careworkerId);
        Recipient recipient = findRecipientByIdAndCareworker(recipientId, careworkerId);

        if (!careworker.getInstitution().getId().equals(recipient.getInstitution().getId())) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        recipient.updateRecipient(recipientDTO);
        return toResponse(recipient);
    }

    // 요양원에 속한 돌봄대상자 정보 수정
    @Transactional
    public RecipientResponse updateRecipientForInstitution(Long recipientId, RecipientUpdateInstitutionRequest recipientDTO, Long institutionId) {
        Recipient recipient = findRecipientByIdAndInstitution(recipientId, institutionId);
        Careworker careworker = careworkerService.getCareworkerById(recipientDTO.getCareworkerId());

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        Guardian guardian = guardianService.findGuardianById(recipientDTO.getGuardianId());

        if (!guardian.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }
        // 요양원은 본인 요양원에 속한 careworker, guardian 업데이트 가능
        recipient.updateRecipientForInstitution(recipientDTO, careworker, guardian);

        return toResponse(recipient);
    }

    //요양보호사가 담당하는 돌봄대상자 삭제
    @Transactional
    public void deleteRecipientForCareworker(Long recipientId, Long careworkerId) {
        Recipient recipient = findRecipientByIdAndCareworker(recipientId, careworkerId);
        recipientRepository.delete(recipient);
    }

    //요양원에 속한 돌봄대상자 삭제
    @Transactional
    public void deleteRecipientForInstitution(Long recipientId, Long institutionId) {
        Recipient recipient = findRecipientByIdAndInstitution(recipientId, institutionId);
        recipientRepository.delete(recipient);
    }

    // 권한별 접근 검증 로직
    private Recipient findRecipientByIdAndCareworker(Long recipientId, Long careworkerId) {
        return recipientRepository.findByIdAndCareworkerId(recipientId, careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED));
    }

    private Recipient findRecipientByIdAndInstitution(Long recipientId, Long institutionId) {
        return recipientRepository.findByIdAndInstitutionId(recipientId, institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED));
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

    private RecipientResponse toResponse(Recipient recipient) {
        return new RecipientResponse(
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
                recipient.getCareworker() != null ? recipient.getCareworker().getId() : null,
                recipient.getGuardian() != null ? recipient.getGuardian().getId() : null
        );
    }

    // 어제 날짜의 차트가 작성되었는지 확인
    public boolean isChartWrittenYesterday(Long guardianId) {
        Recipient recipient = recipientRepository.findByGuardianId(
            guardianId).orElseThrow(() -> new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND
        ));
        Chart chart = getChartByRecipientIdAndDate(recipient.getId(), LocalDate.now().minusDays(1));
        return chart != null;
    }

    // 보호자 ID로 어제 차트 ID 조회
    public Long getChartIdByGuardianId(Long guardianId) {
        Recipient recipient = recipientRepository.findByGuardianId(guardianId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
        Chart chart = getChartByRecipientIdAndDate(recipient.getId(), LocalDate.now().minusDays(1));
        return chart.getId();
    }

    public Chart getChartByRecipientIdAndDate(Long recipientId, LocalDate date) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59);

        return chartRepository.findByRecipientIdAndCreatedAtBetween(recipientId, startDateTime, endDateTime)
            .orElse(null);
    }
}
