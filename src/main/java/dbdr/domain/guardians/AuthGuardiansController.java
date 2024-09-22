package dbdr.domain.guardians;

import jakarta.validation.Valid;
import java.util.List;
import org.hibernate.annotations.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth/guardians")
@Comment("관리자 페이지의 보호자 관리")
public class AuthGuardiansController {

    private final GuardiansService guardiansService;

    public AuthGuardiansController(GuardiansService guardiansService) {
        this.guardiansService = guardiansService;
    }

    @Comment("보호자 전부 조회")
    @GetMapping
    public ResponseEntity<List<GuardiansResponse>> showAllGuardians() {
        List<GuardiansResponse> guardiansResponseList = guardiansService.getAllGuardians();
        return ResponseEntity.ok(guardiansResponseList);
    }

    @Comment("보호자 한 사람 조회")
    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardiansResponse> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardiansResponse guardiansResponse = guardiansService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardiansResponse);
    }

    @Comment("보호자 한 사람 생성")
    @PostMapping
    public ResponseEntity<GuardiansResponse> addGuardian(
        @Valid @RequestBody GuardiansRequest guardiansRequest) throws Exception {
        GuardiansResponse guardiansResponse = guardiansService.addGuardian(guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }

    @Comment("보호자 정보 수정")
    @PutMapping("/{guardianId}/update")
    public ResponseEntity<GuardiansResponse> updateGuardianAuth(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardiansRequest guardiansRequest) {
        GuardiansResponse guardiansResponse = guardiansService.updateGuardianById(guardianId,
            guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }

    @Comment("보호자 한 사람 삭제")
    @PutMapping("/{guardianId}/delete")
    public ResponseEntity<Void> deleteGuardianAuth(
        @PathVariable("guardianId") Long guardianId) {
        guardiansService.deleteGuardianById(guardianId);
        return ResponseEntity.ok().build();
    }
}
