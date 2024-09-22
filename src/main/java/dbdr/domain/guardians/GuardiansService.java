package dbdr.domain.guardians;

import java.util.List;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Service;

@Service
public class GuardiansService {

    private final GuardiansRepository guardiansRepository;

    public GuardiansService(GuardiansRepository guardiansRepository) {
        this.guardiansRepository = guardiansRepository;
    }

    @Comment("보호자 아이디로 조회하기")
    public GuardiansResponse getGuardianById(Long guardianId) {
        Guardians guardian = guardiansRepository.findById(guardianId).orElseThrow();
        return new GuardiansResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    @Comment("보호자 아이디로 수정하기")
    public GuardiansResponse updateGuardianById(Long guardianId,
        GuardiansRequest guardiansRequest) {
        Guardians guardian = guardiansRepository.findById(guardianId).orElseThrow();
        guardian.updateGuardian(guardiansRequest.phone(), guardiansRequest.name());
        guardiansRepository.save(guardian);
        return new GuardiansResponse(guardiansRequest.phone(), guardiansRequest.name(), guardian.isActive());
    }

    @Comment("보호자 전부 조회하기")
    public List<GuardiansResponse> getAllGuardians() {
        List<Guardians> guardians = guardiansRepository.findAll();
        return guardians.stream()
            .map(guardian -> new GuardiansResponse(guardian.getPhone(), guardian.getName(), guardian.isActive()))
            .toList();
    }

    @Comment("보호자 한 사람 새로 추가하기")
    public GuardiansResponse addGuardian(GuardiansRequest guardiansRequest) throws Exception {
        phoneNumberExists(guardiansRequest.phone());
        Guardians guardian = new Guardians(guardiansRequest.phone(), guardiansRequest.name());
        guardian = guardiansRepository.save(guardian);
        return new GuardiansResponse(guardian.getPhone(), guardian.getName(), guardian.isActive());
    }

    @Comment("보호자 한 사람 삭제하기(DB에는 존재)")
    public void deleteGuardianById(Long guardianId) {
        Guardians guardians = guardiansRepository.findById(guardianId).orElseThrow();
        guardians.deactivate();
        guardiansRepository.save(guardians);
    }

    private void phoneNumberExists(String phone) throws Exception {
        if(guardiansRepository.existsByPhone(phone)){
            throw new Exception("존재하는 전화번호입니다.");
        }
    }
}
