package dbdr.domain.guardian.service;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.repository.GuardianRepository;
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
        Guardian guardian = guardianRepository.findById(guardianId).orElseThrow();
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public GuardianResponse updateGuardianById(Long guardianId,
        GuardianRequest guardianRequest) {
        Guardian guardian = guardianRepository.findById(guardianId).orElseThrow();
        guardian.updateGuardian(guardianRequest.phone(), guardianRequest.name(), passwordEncoding(guardianRequest.loginPassword()));
        guardianRepository.save(guardian);
        return new GuardianResponse(guardianRequest.phone(), guardianRequest.name(), guardian.isActive());
    }

    public List<GuardianResponse> getAllGuardian() {
        List<Guardian> guardianList = guardianRepository.findAll();
        return guardianList.stream()
            .map(guardian -> new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive()))
            .toList();
    }

    public GuardianResponse addGuardian(GuardianRequest guardianRequest) {
        phoneNumberExists(guardianRequest.phone());
        Guardian guardian = new Guardian(guardianRequest.phone(), guardianRequest.name(), passwordEncoding(guardianRequest.loginPassword()));
        guardian = guardianRepository.save(guardian);
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public void deleteGuardianById(Long guardianId) {
        Guardian guardian = guardianRepository.findById(guardianId).orElseThrow();
        guardian.deactivate();
        guardianRepository.save(guardian);
    }

    private void phoneNumberExists(String phone) {
        if(guardianRepository.existsByPhone(phone)){
            throw new IllegalArgumentException("존재하는 전화번호입니다.");
        }
    }

    private String passwordEncoding(String password) {
        return passwordEncoder.encode(password);
    }
}
