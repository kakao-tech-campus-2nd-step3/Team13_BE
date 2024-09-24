package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.service.GuardianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/guardian")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardiansService;

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showGuardianInfo(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardiansResponse
            = guardiansService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardiansResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardianInfo(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardiansRequest) {
        GuardianResponse guardiansResponse
            = guardiansService.updateGuardianById(guardianId, guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }
}
