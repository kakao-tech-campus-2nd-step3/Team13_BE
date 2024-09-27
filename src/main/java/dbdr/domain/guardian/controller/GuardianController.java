package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.service.GuardianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/guardian")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('GUARDIAN','ADMIN')")
public class GuardianController {

    private final GuardianService guardianService;

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showGuardianInfo(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardianResponse
            = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardianResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardianInfo(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse
            = guardianService.updateGuardianById(guardianId, guardianRequest);
        return ResponseEntity.ok(guardianResponse);
    }
}
