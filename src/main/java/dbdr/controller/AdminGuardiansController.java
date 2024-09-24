package dbdr.controller;

import dbdr.dto.request.GuardiansRequest;
import dbdr.dto.response.GuardiansResponse;
import dbdr.service.GuardiansService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/guardians")
@RequiredArgsConstructor
public class AdminGuardiansController {

    private final GuardiansService guardiansService;

    @GetMapping
    public ResponseEntity<List<GuardiansResponse>> showAllGuardians() {
        List<GuardiansResponse> guardiansResponseList = guardiansService.getAllGuardians();
        return ResponseEntity.ok(guardiansResponseList);
    }

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardiansResponse> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardiansResponse guardiansResponse = guardiansService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardiansResponse);
    }

    @PostMapping
    public ResponseEntity<GuardiansResponse> addGuardian(
        @Valid @RequestBody GuardiansRequest guardiansRequest) {
        GuardiansResponse guardiansResponse = guardiansService.addGuardian(guardiansRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardiansResponse);
    }

    @PutMapping("/{guardianId}/update")
    public ResponseEntity<GuardiansResponse> updateGuardianAuth(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardiansRequest guardiansRequest) {
        GuardiansResponse guardiansResponse = guardiansService.updateGuardianById(guardianId,
            guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }

    @PutMapping("/{guardianId}/delete")
    public ResponseEntity<Void> deleteGuardianAuth(
        @PathVariable("guardianId") Long guardianId) {
        guardiansService.deleteGuardianById(guardianId);
        return ResponseEntity.noContent().build();
    }
}
