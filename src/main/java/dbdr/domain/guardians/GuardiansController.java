package dbdr.domain.guardians;

import jakarta.validation.Valid;
import org.hibernate.annotations.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/guardians")
@Comment("보호자 관련 API")
public class GuardiansController {

    private final GuardiansService guardiansService;

    public GuardiansController(GuardiansService guardiansService) {
        this.guardiansService = guardiansService;
    }

    @Comment("보호자 자신 정보 조회")
    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardiansResponse> showGuardianInfo(
        @PathVariable("guardianId") Long guardianId) {
        GuardiansResponse guardiansResponse = guardiansService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardiansResponse);
    }

    @Comment("보호자 자신 정보 수정")
    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardiansResponse> updateGuardianInfo(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardiansRequest guardiansRequest) {
        GuardiansResponse guardiansResponse = guardiansService.updateGuardianById(guardianId,
            guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }
}
