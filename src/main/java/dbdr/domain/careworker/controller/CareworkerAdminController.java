package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.util.api.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "전체 요양보호사 정보 조회")
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<CareworkerResponseDTO>>> getAllCareworkers() {
        List<CareworkerResponseDTO> careworkers = careworkerService.getAllCareworkers();
        return ResponseEntity.ok(ApiUtils.success(careworkers));
    }


    @Operation(summary = "요양보호사 정보 조회")
    @GetMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> getCareworkerById(
            @PathVariable Long careworkerId) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerResponseById(careworkerId);
        return ResponseEntity.ok(ApiUtils.success(careworker));
    }

    @Operation(summary = "요양보호사 추가")
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> createCareworker(
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO, careworkerDTO.getInstitutionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newCareworker));

    }


    @Operation(summary = "요양보호사 정보 수정 ")
    @PutMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> updateCareworker(
            @PathVariable Long careworkerId,
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworkerByAdmin(careworkerId, careworkerDTO);
        return ResponseEntity.ok(ApiUtils.success(updatedCareworker));
    }


    @Operation(summary = "요양보호사 삭제")
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteCareworker(@PathVariable Long careworkerId) {
        careworkerService.deleteCareworkerByAdmin(careworkerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
