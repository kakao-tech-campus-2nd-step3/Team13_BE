package dbdr.domain.guardian.service;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardiansRepository;

    public GuardianResponse getGuardianById(Long guardianId) {
        Guardian guardian = guardiansRepository.findById(guardianId).orElseThrow();
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public GuardianResponse updateGuardianById(Long guardianId,
        GuardianRequest guardiansRequest) {
        Guardian guardian = guardiansRepository.findById(guardianId).orElseThrow();
        guardian.updateGuardian(guardiansRequest.phone(), guardiansRequest.name());
        guardiansRepository.save(guardian);
        return new GuardianResponse(guardiansRequest.phone(), guardiansRequest.name(), guardian.isActive());
    }

    public List<GuardianResponse> getAllGuardians() {
        List<Guardian> guardians = guardiansRepository.findAll();
        return guardians.stream()
            .map(guardian -> new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive()))
            .toList();
    }

    public GuardianResponse addGuardian(GuardianRequest guardiansRequest) {
        phoneNumberExists(guardiansRequest.phone());
        Guardian guardian = new Guardian(guardiansRequest.phone(), guardiansRequest.name());
        guardian = guardiansRepository.save(guardian);
        return new GuardianResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public void deleteGuardianById(Long guardianId) {
        Guardian guardians = guardiansRepository.findById(guardianId).orElseThrow();
        guardians.deactivate();
        guardiansRepository.save(guardians);
    }

    private void phoneNumberExists(String phone) {
        if(guardiansRepository.existsByPhone(phone)){
            throw new IllegalArgumentException("존재하는 전화번호입니다.");
        }
    }
}
