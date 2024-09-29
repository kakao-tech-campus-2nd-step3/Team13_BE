package dbdr.domain.guardian.service;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.exception.ApplicationError;
import dbdr.exception.ApplicationException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;

    private final PasswordEncoder passwordEncoder;

    public GuardianResponse getGuardianById(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public GuardianResponse updateGuardianById(Long guardianId,
        GuardianRequest guardianRequest) {
        phoneNumberExists(guardianRequest.phone());

        Guardian guardian = findGuardianById(guardianId);
        guardian.updateGuardian(guardianRequest.phone(), guardianRequest.name(), passwordEncoding(guardianRequest.loginPassword()));
        guardianRepository.save(guardian);

        return new GuardianResponse(guardianRequest.phone(), guardianRequest.name(),
            guardian.isActive());
    }

    public List<GuardianResponse> getAllGuardian() {
        List<Guardian> guardianList = guardianRepository.findAll();
        return guardianList.stream()
            .map(guardian -> new GuardianResponse(guardian.getPhone(), guardian.getName(),
                guardian.isActive()))
            .toList();
    }

    public GuardianResponse addGuardian(GuardianRequest guardianRequest) {
        phoneNumberExists(guardianRequest.phone());
        Guardian guardian = new Guardian(guardianRequest.phone(), guardianRequest.name(), passwordEncoding(guardianRequest.loginPassword()));
        guardian = guardianRepository.save(guardian);
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public void deleteGuardianById(Long guardianId) {
        Guardian guardian = findGuardianById(guardianId);
        guardian.deactivate();
        guardianRepository.delete(guardian);
    }

    private Guardian findGuardianById(Long guardianId) {
        return guardianRepository.findById(guardianId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));
    }

    private void phoneNumberExists(String phone) {
        if (guardianRepository.existsByPhone(phone)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private String passwordEncoding(String password) {
        return passwordEncoder.encode(password);
    }
}
