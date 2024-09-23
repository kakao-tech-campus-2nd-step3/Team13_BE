package dbdr.service;

import dbdr.domain.Guardians;
import dbdr.dto.request.GuardiansRequest;
import dbdr.dto.response.GuardiansResponse;
import dbdr.repository.GuardiansRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GuardiansService {

    private final GuardiansRepository guardiansRepository;

    public GuardiansService(GuardiansRepository guardiansRepository) {
        this.guardiansRepository = guardiansRepository;
    }

    public GuardiansResponse getGuardianById(Long guardianId) {
        Guardians guardian = guardiansRepository.findById(guardianId).orElseThrow();
        return new GuardiansResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public GuardiansResponse updateGuardianById(Long guardianId,
        GuardiansRequest guardiansRequest) {
        Guardians guardian = guardiansRepository.findById(guardianId).orElseThrow();
        guardian.updateGuardian(guardiansRequest.phone(), guardiansRequest.name());
        guardiansRepository.save(guardian);
        return new GuardiansResponse(guardiansRequest.phone(), guardiansRequest.name(), guardian.isActive());
    }

    public List<GuardiansResponse> getAllGuardians() {
        List<Guardians> guardians = guardiansRepository.findAll();
        return guardians.stream()
            .map(guardian -> new GuardiansResponse(guardian.getPhone(), guardian.getName(), guardian.isActive()))
            .toList();
    }

    public GuardiansResponse addGuardian(GuardiansRequest guardiansRequest) {
        phoneNumberExists(guardiansRequest.phone());
        Guardians guardian = new Guardians(guardiansRequest.phone(), guardiansRequest.name());
        guardian = guardiansRepository.save(guardian);
        return new GuardiansResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    public void deleteGuardianById(Long guardianId) {
        Guardians guardians = guardiansRepository.findById(guardianId).orElseThrow();
        guardians.deactivate();
        guardiansRepository.save(guardians);
    }

    private void phoneNumberExists(String phone) {
        if(guardiansRepository.existsByPhone(phone)){
            throw new IllegalArgumentException("존재하는 전화번호입니다.");
        }
    }
}
