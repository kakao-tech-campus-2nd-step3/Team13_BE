package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.request.GuardianUpdateRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.institution.entity.Institution;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginInstitution;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "[요양원] 보호자 관리", description = "보호자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/institution/guardian")
@RequiredArgsConstructor
public class GuardianInstitutionController {

    private final GuardianService guardianService;

    @Operation(summary = "전체 보호자 정보 조회")
    @GetMapping
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.LOGIN_INSTITUTION)
    public ResponseEntity<ApiUtils.ApiResult<List<GuardianResponse>>> showAllGuardian(
        @Parameter(hidden = true) @LoginInstitution Institution institution) {
        List<GuardianResponse> guardianResponseList = guardianService.getAllGuardianByInstitutionId(
            institution.getId());
        return ResponseEntity.ok(ApiUtils.success(guardianResponseList));
    }

    @Operation(summary = "보호자 한 사람의 정보 조회")
    @GetMapping("/{guardianId}")
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.GUARDIAN_ID, id = "guardianId")
    public ResponseEntity<ApiUtils.ApiResult<GuardianResponse>> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(ApiUtils.success(guardianResponse));
    }

    @Operation(summary = "보호자 추가")
    @PostMapping
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.LOGIN_INSTITUTION)
    public ResponseEntity<ApiUtils.ApiResult<GuardianResponse>> addGuardian(
        @Valid @RequestBody GuardianRequest guardianRequest,
        @Parameter(hidden = true) @LoginInstitution Institution institution) {
        GuardianResponse guardianResponse = guardianService.addGuardianByInstitution(
            guardianRequest, institution.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(guardianResponse));
    }

    @Operation(summary = "보호자 정보 수정")
    @PutMapping("/{guardianId}")
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.GUARDIAN_ID, id = "guardianId")
    public ResponseEntity<ApiUtils.ApiResult<GuardianResponse>> updateGuardian(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianUpdateRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardianId,
            guardianRequest);
        return ResponseEntity.ok(ApiUtils.success(guardianResponse));
    }

    @Operation(summary = "보호자 삭제")
    @DeleteMapping("/{guardianId}")
    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.GUARDIAN_ID, id = "guardianId")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteGuardian(
        @PathVariable("guardianId") Long guardianId) {
        guardianService.deleteGuardianById(guardianId);
        return ResponseEntity.noContent().build();
    }
}
