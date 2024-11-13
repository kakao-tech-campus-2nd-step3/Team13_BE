package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.request.CareworkerUpdateAdminRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "[관리자] 요양보호사 관리", description = "관리자가 요양사 정보를 조회, 수정, 추가, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/admin/careworker")
@RequiredArgsConstructor
public class CareworkerAdminController {

    private final CareworkerService careworkerService;
    private final InstitutionService institutionService;

    @DbdrAuth(targetRole = Role.ADMIN)
    @Operation(summary = "전체 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<CareworkerResponse>>> getAllCareworkers() {
        List<CareworkerResponse> careworkers = careworkerService.getAllCareworkers();
        return ResponseEntity.ok(ApiUtils.success(careworkers));
    }

    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.CAREWORKER_ID, id="careworkerId")
    @Operation(summary = "요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponse>> getCareworkerById(
            @PathVariable Long careworkerId) {
        CareworkerResponse careworker = careworkerService.getCareworkerResponseById(careworkerId);
        return ResponseEntity.ok(ApiUtils.success(careworker));
    }

    @DbdrAuth(targetRole = Role.ADMIN)
    @Operation(summary = "요양보호사 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponse>> createCareworker(
            @Valid @RequestBody CareworkerRequest careworkerDTO) {
        CareworkerResponse newCareworker = careworkerService.createCareworker(careworkerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newCareworker));

    }

    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.CAREWORKER_ID, id="careworkerId")
    @Operation(summary = "요양보호사 정보 수정 ", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponse>> updateCareworker(
            @PathVariable Long careworkerId,
            @Valid @RequestBody CareworkerUpdateAdminRequest careworkerDTO) {
        CareworkerResponse updatedCareworker = careworkerService.updateCareworkerByAdmin(careworkerId, careworkerDTO);
        return ResponseEntity.ok(ApiUtils.success(updatedCareworker));
    }

    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.CAREWORKER_ID, id="careworkerId")
    @Operation(summary = "요양보호사 삭제")
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteCareworker(@PathVariable Long careworkerId) {
        careworkerService.deleteCareworkerByAdmin(careworkerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
