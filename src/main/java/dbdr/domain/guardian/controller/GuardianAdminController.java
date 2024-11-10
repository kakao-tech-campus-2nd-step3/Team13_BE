package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.util.api.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "[관리자] 보호자 (Guardian)", description = "보호자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/admin/guardian")
@RequiredArgsConstructor
public class GuardianAdminController {

    private final GuardianService guardianService;

    @Operation(summary = "전체 보호자 정보 조회")
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<GuardianResponse>>> showAllGuardian() {
        List<GuardianResponse> guardianResponseList = guardianService.getAllGuardian();
        return ResponseEntity.ok(ApiUtils.success(guardianResponseList));
    }

    @Operation(summary = "보호자 한 사람의 정보 조회")
    @GetMapping("/{guardianId}")
    public ResponseEntity<ApiUtils.ApiResult<GuardianResponse>> showOneGuardian(
        @PathVariable("guardianId") Long guardianId) {
        GuardianResponse guardianResponse = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(ApiUtils.success(guardianResponse));
    }

    @Operation(summary = "보호자 추가")
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<GuardianResponse>> addGuardian(
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.addGuardian(guardianRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(guardianResponse));
    }

    @Operation(summary = "보호자 정보 수정")
    @PutMapping("/{guardianId}")
    public ResponseEntity<ApiUtils.ApiResult<GuardianResponse>> updateGuardian(
        @PathVariable("guardianId") Long guardianId,
        @Valid @RequestBody GuardianRequest guardianRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardianById(guardianId,
            guardianRequest);
        return ResponseEntity.ok(ApiUtils.success(guardianResponse));
    }

    @Operation(summary = "보호자 삭제")
    @DeleteMapping("/{guardianId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteGuardian(@PathVariable("guardianId") Long guardianId) {
        guardianService.deleteGuardianById(guardianId);
        return ResponseEntity.noContent().build();
    }
}
