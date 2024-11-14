package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.request.CareworkerUpdateInstitutionRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.entity.Institution;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginInstitution;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "[요양원] 요양보호사 관리  ", description = "요양보호사 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/institution/careworker")
@RequiredArgsConstructor
public class CareworkerInstitutionController {

    private final CareworkerService careworkerService;

    @DbdrAuth(targetRole = Role.INSTITUTION)
    @Operation(summary = "특정 요양원아이디로 전체 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<CareworkerResponse>>> getAllCareworkers(
            @Parameter(hidden = true) @LoginInstitution Institution institution) {
        List<CareworkerResponse> institutions = careworkerService.getCareworkersByInstitution(institution.getId());
        return ResponseEntity.ok(ApiUtils.success(institutions)) ;
    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.CAREWORKER_ID, id="careworkerId")
    @Operation(summary = "요양보호사 아이디로 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponse>> getCareworkerById(
            @PathVariable("careworkerId") Long careworkerId,
            @Parameter(hidden = true) @LoginInstitution Institution institution) {
        CareworkerResponse careworker = careworkerService.getCareworkerByInstitution(careworkerId, institution.getId());
        return ResponseEntity.ok(ApiUtils.success(careworker)) ;
    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.LOGIN_INSTITUTION)
    @Operation(summary = "요양보호사 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponse>> createCareworker(
            @Parameter(hidden = true) @LoginInstitution Institution institution,
            @Valid @RequestBody CareworkerRequest careworkerDTO) {
        CareworkerResponse newCareworker = careworkerService.createCareworkerInstitution(careworkerDTO, institution.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newCareworker));

    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.CAREWORKER_ID, id="careworkerId")
    @Operation(summary = "요양보호사 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponse>> updateCareworker(
            @PathVariable Long careworkerId,
            @Parameter(hidden = true) @LoginInstitution Institution institution,
            @RequestBody CareworkerUpdateInstitutionRequest careworkerDTO) {
        CareworkerResponse updatedCareworker = careworkerService.updateCareworkerByInstitution(careworkerId, careworkerDTO);
        return ResponseEntity.ok(ApiUtils.success(updatedCareworker));
    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.CAREWORKER_ID, id="careworkerId")
    @Operation(summary = "요양보호사 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteCareworker(
            @PathVariable Long careworkerId,
            @Parameter(hidden = true) @LoginInstitution Institution institution) {
        careworkerService.deleteCareworker(careworkerId, institution.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
