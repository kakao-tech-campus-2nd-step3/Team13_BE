package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import dbdr.security.LoginGuardian;
import dbdr.security.dto.BaseUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "보호자 (Guardian)", description = "보호자 정보 조회, 수정")
@RestController
@RequestMapping("/${spring.app.version}/guardian")
@RequiredArgsConstructor
@Slf4j
public class GuardianController {

    private final GuardianService guardianService;

    @Operation(summary = "보호자 본인의 정보 조회")
    @GetMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> showGuardianInfo(
        @PathVariable("guardianId") Long guardianId, @LoginGuardian Guardian gaurdian) {
        log.info("guardianId: {}", gaurdian.getName());
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(guardianResponse);
    }

    @Operation(summary = "보호자 본인의 정보 수정")
    @PutMapping("/{guardianId}")
    public ResponseEntity<GuardianResponse> updateGuardianInfo(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardianId,
            guardianRequest);
        return ResponseEntity.ok(guardianResponse);
    }
}
