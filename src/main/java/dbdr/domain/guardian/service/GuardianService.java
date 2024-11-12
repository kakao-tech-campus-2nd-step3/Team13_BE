package dbdr.domain.guardian.service;

import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.guardian.dto.request.GuardianAlertTimeRequest;
import dbdr.domain.guardian.dto.request.GuardianUpdateRequest;
import dbdr.domain.guardian.dto.response.GuardianMyPageResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final InstitutionRepository institutionRepository;
    private final AlarmService alarmService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public GuardianResponse getGuardianById(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        return new GuardianResponse(guardianId, guardian.getPhone(), guardian.getName(),
            guardian.getInstitution().getId(), guardian.isActive());
    }

    public GuardianMyPageResponse getMyPageGuardianInfo(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        return new GuardianMyPageResponse(guardian.getName(), guardian.getPhone(),
            guardian.getAlertTime());
    }

    @Transactional
    public GuardianMyPageResponse updateAlertTime(Long guardianId,
        GuardianAlertTimeRequest request) {
        ensureUniquePhoneButNotId(request.phone(), guardianId);
        Guardian guardian = findGuardianById(guardianId);
        guardian.updateAlertTime(request.name(), request.phone(), request.alertTime());
        guardianRepository.save(guardian);
        alarmService.updateAlarmByLocalTime(request.alertTime(), request.phone());
        return new GuardianMyPageResponse(guardian.getName(), guardian.getPhone(),
            guardian.getAlertTime());
    }

    @Transactional
    public GuardianResponse updateGuardianById(
        Long guardianId,
        GuardianUpdateRequest guardianRequest
    ) {
        ensureUniquePhoneButNotId(guardianRequest.phone(), guardianId);

        Guardian guardian = findGuardianById(guardianId);
        guardian.updateGuardian(guardianRequest.phone(), guardianRequest.name());
        guardianRepository.save(guardian);
        return new GuardianResponse(guardianId, guardianRequest.phone(), guardianRequest.name(),
            guardian.getInstitution().getId(),
            guardian.isActive());
    }

    @Transactional(readOnly = true)
    public List<GuardianResponse> getAllGuardian() {
        List<Guardian> guardianList = guardianRepository.findAll();
        return guardianList.stream()
            .map(guardian -> new GuardianResponse(guardian.getId(), guardian.getPhone(),
                guardian.getName(),
                guardian.getInstitution().getId(),
                guardian.isActive()))
            .toList();
    }

    @Transactional
    public GuardianResponse addGuardian(GuardianRequest guardianRequest) {
        ensureUniquePhone(guardianRequest.phone());
        Institution institution = institutionRepository.findById(guardianRequest.institutionId())
            .orElseThrow(() -> new ApplicationException(
                ApplicationError.INSTITUTION_NOT_FOUND));
        String password = passwordEncoder.encode(guardianRequest.loginPassword());
        Guardian guardian = Guardian.builder().phone(guardianRequest.phone())
            .name(guardianRequest.name())
            .phone(guardianRequest.phone())
            .loginPassword(password)
            .institution(institution)
            .build();
        guardian = guardianRepository.save(guardian);
        alarmService.createGuardianAlarm(guardian);
        return new GuardianResponse(guardian.getId(), guardian.getPhone(), guardian.getName(),
            guardian.getInstitution().getId(), guardian.isActive());
    }

    @Transactional
    public void deleteGuardianById(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        guardian.deactivate();
        guardianRepository.delete(guardian);
    }

    public Guardian findGuardianById(Long guardianId) {
        return guardianRepository.findById(guardianId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));
    }

    private void ensureUniquePhone(String phone) {
        if (guardianRepository.existsByPhone(phone)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private void ensureUniquePhoneButNotId(String phone, Long id) {
        if (guardianRepository.existsByPhoneAndIdNot(phone, id)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    public Guardian findByLineUserId(String userId) {
        return guardianRepository.findByLineUserId(userId)
            .orElse(null);
    }

    public Guardian findByPhone(String phone) {
        return guardianRepository.findByPhone(phone)
            .orElse(null);
    }

    @Transactional
    public void updateLineUserId(String userId, String phoneNumber) {
        Guardian guardian = findByPhone(phoneNumber);
        guardian.updateLineUserId(userId);
        guardianRepository.save(guardian);
    }

	public List<Guardian> findByAlertTime(LocalTime currentTime) {
        return guardianRepository.findByAlertTime(currentTime);
	}
}
