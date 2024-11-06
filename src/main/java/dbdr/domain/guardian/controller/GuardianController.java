package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import dbdr.security.LoginGuardian;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "보호자 본인의 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<GuardianResponse> showGuardianInfo(
        @Parameter(hidden = true) @LoginGuardian Guardian guardian) {
        log.info("guardianId: {}", guardian.getName());
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardian.getId());
        return ResponseEntity.ok(guardianResponse);
    }

    @Operation(summary = "보호자 본인의 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping
    public ResponseEntity<GuardianResponse> updateGuardianInfo(
        @Parameter(hidden = true) @Valid @RequestBody GuardianRequest guardianRequest,
        @LoginGuardian Guardian guardian) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardian.getId(),
            guardianRequest);
        return ResponseEntity.ok(guardianResponse);
    }
}
