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

    private final GuardianService guardianService;

    @GetMapping
    public ResponseEntity<List<GuardianResponse>> showAllGuardian() {
        List<GuardianResponse> guardiansResponseList = guardianService.getAllGuardian();
        return ResponseEntity.ok(guardiansResponseList);
    }

    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardianResponse);
    }

    @PostMapping
    public ResponseEntity<GuardianResponse> addGuardian(
        @Valid @RequestBody GuardianRequest guardiansRequest) {
        GuardianResponse guardianResponse = guardianService.addGuardian(guardiansRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardianResponse);
    }

    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardianAuth(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardiansRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardianId,
            guardiansRequest);
        return ResponseEntity.ok(guardianResponse);
    }

    @DeleteMapping("/{guardianId}")
    public ResponseEntity<Void> deleteGuardianAuth(
        @PathVariable("guardianId") Long guardianId) {
        guardianService.deleteGuardianById(guardianId);
        return ResponseEntity.noContent().build();
    }
}
