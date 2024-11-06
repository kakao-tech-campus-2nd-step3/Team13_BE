package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.security.LoginCareworker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "[관리자] 요양보호사 (Careworker)", description = "요양보호사 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/careworker")
@RequiredArgsConstructor
public class CareworkerController {

    private final CareworkerService careworkerService;

    @Operation(summary = "특정 요양원아이디로 전체 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<List<CareworkerResponseDTO>> getAllCareworkers(
            @RequestParam("institutionId") @NotNull Long institutionId) {
        List<CareworkerResponseDTO> careworkerList = careworkerService.getCareworkersByInstitution(institutionId);
        return ResponseEntity.ok(careworkerList);
    }

    @Operation(summary = "요양보호사 한 사람의 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{careworkerId}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
            @PathVariable("careworkerId") Long careworkerId) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerResponseById(careworkerId);
        return ResponseEntity.ok(careworker);
    }

    @Operation(summary = "요양보호사 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/{institutionId}")
    public ResponseEntity<CareworkerResponseDTO> createCareworker(
            @PathVariable Long institutionId,
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO, institutionId);
        return ResponseEntity.created(
                        URI.create("/" + institutionId + "/careworker/" + newCareworker.getId()))
                .body(newCareworker);
    }

    @Operation(summary = "요양보호사 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{careworkerId}")
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(
            @PathVariable Long careworkerId,
            @RequestParam("institutionId") @NotNull Long institutionId,
            @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(careworkerId, careworkerDTO, institutionId);
        return ResponseEntity.ok(updatedCareworker);
    }

    @Operation(summary = "요양보호사 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<Void> deleteCareworker(
            @PathVariable Long careworkerId,
            @RequestParam("institutionId") @NotNull Long institutionId) {
        careworkerService.deleteCareworker(careworkerId, institutionId);
        return ResponseEntity.noContent().build();
    }
}
