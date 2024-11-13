package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianMyPageRequest;
import dbdr.domain.guardian.dto.response.GuardianMyPageResponse;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
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

@Tag(name = "[보호자] 마이페이지", description = "보호자 본인의 정보 조회, 수정")
@RestController
@RequestMapping("/${spring.app.version}/guardian")
@RequiredArgsConstructor
@Slf4j
public class GuardianController {

    private final GuardianService guardianService;

    @Operation(summary = "보호자 본인의 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    @DbdrAuth(targetRole = Role.GUARDIAN, authParam = AuthParam.LOGIN_GUARDIAN)
    public ResponseEntity<ApiUtils.ApiResult<GuardianMyPageResponse>> showGuardianInfo(
        @Parameter(hidden = true) @LoginGuardian Guardian guardian) {
        log.info("guardianId: {}", guardian.getName());
        GuardianMyPageResponse guardianMyPageResponse = guardianService.getMyPageGuardianInfo(
            guardian.getId());
        return ResponseEntity.ok(ApiUtils.success(guardianMyPageResponse));
    }

    @Operation(summary = "보호자 본인의 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping
    @DbdrAuth(targetRole = Role.GUARDIAN, authParam = AuthParam.LOGIN_GUARDIAN)
    public ResponseEntity<ApiUtils.ApiResult<GuardianMyPageResponse>> updateGuardianInfo(
        @Valid @RequestBody GuardianMyPageRequest guardianMyPageRequest,
        @Parameter(hidden = true) @LoginGuardian Guardian guardian) {
        GuardianMyPageResponse guardianMyPageResponse =
            guardianService.updateMyPageInfo(guardian.getId(), guardianMyPageRequest);
        return ResponseEntity.ok(ApiUtils.success(guardianMyPageResponse));
    }
}
