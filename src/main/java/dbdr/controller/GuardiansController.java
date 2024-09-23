package dbdr.controller;

import dbdr.dto.request.GuardiansRequest;
import dbdr.dto.response.GuardiansResponse;
import dbdr.service.GuardiansService;
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
@RequestMapping("/v1/guardians")
@RequiredArgsConstructor
public class GuardiansController {

    private final GuardiansService guardiansService;

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardiansResponse> showGuardianInfo(
        @PathVariable("guardianId") Long guardianId) {
        GuardiansResponse guardiansResponse = guardiansService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardiansResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardiansResponse> updateGuardianInfo(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardiansRequest guardiansRequest) {
        GuardiansResponse guardiansResponse = guardiansService.updateGuardianById(guardianId,
            guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }
}
