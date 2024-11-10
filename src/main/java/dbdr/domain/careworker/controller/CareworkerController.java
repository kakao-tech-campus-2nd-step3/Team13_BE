package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerUpdateRequest;
import dbdr.domain.careworker.dto.response.CareworkerMyPageResponse;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginCareworker;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "[요양보호사] 마이페이지", description = "요양보호사 본인의 정보 조회 및 수정")
@RestController
@RequestMapping("/${spring.app.version}/careworker")
@RequiredArgsConstructor
@Slf4j
public class CareworkerController {

    private final CareworkerService careworkerService;

    @Operation(summary = "요양보호사 본인의 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    @DbdrAuth(targetRole = Role.CAREWORKER)
    public ResponseEntity<ApiUtils.ApiResult<CareworkerMyPageResponse>> showCareworkerInfo(
            @Parameter(hidden = true) @LoginCareworker Careworker careworker) {
        log.info("Careworker Name: {}", careworker.getName());
        CareworkerMyPageResponse response = careworkerService.getMyPageInfo(careworker.getId());
        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @Operation(summary = "요양보호사 본인의 근무일과 알림 시간 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping
    public ResponseEntity<ApiUtils.ApiResult<CareworkerMyPageResponse>> updateCareworkerInfo(
            @Parameter(hidden = true) @LoginCareworker Careworker careworker,
            @Valid @RequestBody CareworkerUpdateRequest careworkerRequest) {
        CareworkerMyPageResponse updatedResponse = careworkerService.updateWorkingDaysAndAlertTime(careworker.getId(),
                careworkerRequest);
        return ResponseEntity.ok(ApiUtils.success(updatedResponse));
    }
}
