package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.service.GuardianService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/guardian")
@RequiredArgsConstructor
public class GuardianAdminController {

    private final GuardianService guardiansService;

    @GetMapping
    public ResponseEntity<List<GuardianResponse>> showAllGuardians() {
        List<GuardianResponse> guardiansResponseList = guardiansService.getAllGuardians();
        return ResponseEntity.ok(guardiansResponseList);
    }

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardiansResponse = guardiansService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardiansResponse);
    }

    @PostMapping
    public ResponseEntity<GuardianResponse> addGuardian(
        @Valid @RequestBody GuardianRequest guardiansRequest) {
        GuardianResponse guardiansResponse = guardiansService.addGuardian(guardiansRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardiansResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardianAuth(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardiansRequest) {
        GuardianResponse guardiansResponse = guardiansService.updateGuardianById(guardianId,
            guardiansRequest);
        return ResponseEntity.ok(guardiansResponse);
    }

    @DeleteMapping("/{guardianId}")
    public ResponseEntity<Void> deleteGuardianAuth(
        @PathVariable("guardianId") Long guardianId) {
        guardiansService.deleteGuardianById(guardianId);
        return ResponseEntity.noContent().build();
    }
}
