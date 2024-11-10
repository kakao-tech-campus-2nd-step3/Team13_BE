package dbdr.domain.guardian.service;

import dbdr.domain.guardian.dto.request.GuardianAlertTimeRequest;
import dbdr.domain.guardian.dto.response.GuardianMyPageResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public GuardianResponse getGuardianById(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public GuardianMyPageResponse getMyPageGuardianInfo(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        return new GuardianMyPageResponse(guardian.getName(), guardian.getPhone(),
            guardian.getLoginId(), guardian.getAlertTime());
    }

    public GuardianMyPageResponse updateAlertTime(Long guardianId,
        GuardianAlertTimeRequest request) {
        ensureUniquePhoneButNotId(request.phone(), guardianId);
        Guardian guardian = findGuardianById(guardianId);
        guardian.updateAlertTime(request.name(), request.phone(), request.alertTime());
        guardianRepository.save(guardian);
        return new GuardianMyPageResponse(guardian.getName(), guardian.getPhone(),
            guardian.getLoginId(), guardian.getAlertTime());
    }

    @Transactional
    public GuardianResponse updateGuardianById(
            Long guardianId,
            GuardianRequest guardianRequest
    ) {
        ensureUniquePhoneButNotId(guardianRequest.phone(), guardianId);

        Guardian guardian = findGuardianById(guardianId);
        guardian.updateGuardian(guardianRequest.phone(), guardianRequest.name());
        guardianRepository.save(guardian);
        return new GuardianResponse(guardianRequest.phone(), guardianRequest.name(),
            guardian.isActive());
    }

    @Transactional(readOnly = true)
    public List<GuardianResponse> getAllGuardian() {
        List<Guardian> guardianList = guardianRepository.findAll();
        return guardianList.stream()
            .map(guardian -> new GuardianResponse(guardian.getPhone(), guardian.getName(),
                guardian.isActive()))
            .toList();
    }

    @Transactional
    public GuardianResponse addGuardian(GuardianRequest guardianRequest) {
        ensureUniquePhone(guardianRequest.phone());
        String password = passwordEncoder.encode(guardianRequest.loginPassword());
        Guardian guardian = Guardian.builder().phone(guardianRequest.phone())
            .name(guardianRequest.name())
            .loginId(guardianRequest.phone())
            .loginPassword(password)
            .build();
        guardian = guardianRepository.save(guardian);
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    @Transactional
    public void deleteGuardianById(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        guardian.deactivate();
        guardianRepository.delete(guardian);
    }

    private Guardian findGuardianById(Long guardianId) {
        return guardianRepository.findById(guardianId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));
    }

    private void ensureUniquePhone(String phone) {
        if (guardianRepository.existsByPhone(phone)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private void ensureUniquePhoneButNotId(String phone, Long id) {
        if(guardianRepository.existsByPhoneAndIdNot(phone, id)) {
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
}
