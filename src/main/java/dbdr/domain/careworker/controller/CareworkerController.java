package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "[관리자] 요양보호사 관리 (Careworker)")
@RestController
@RequestMapping("/${spring.app.version}/careworker")
@RequiredArgsConstructor
public class CareworkerController {

    private final CareworkerService careworkerService;

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "요양보호사 모두 조회")
    @GetMapping
    public ResponseEntity<List<CareworkerResponseDTO>> getAllCareworkers(
        @RequestParam Long institutionId) {
        List<CareworkerResponseDTO> careworkerList =
                 careworkerService.getCareworkersByInstitution(institutionId);
        return ResponseEntity.ok(careworkerList);
    }

    @Operation(summary = "특정 요양보호사 조회")
    @GetMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
        @PathVariable Long id) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerById(id);
        return ResponseEntity.ok(careworker);
    }

    @Operation(summary = "요양보호사 정보 저장")
    @PostMapping
    public ResponseEntity<CareworkerResponseDTO> createCareworker(
        @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO);
        return ResponseEntity.created(
                URI.create("/" + appVersion + "/careworker/" + newCareworker.getId()))
            .body(newCareworker);
    }

    @Operation(summary = "요양보호사 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(@PathVariable Long id,
        @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(id,
            careworkerDTO);
        return ResponseEntity.ok(updatedCareworker);
    }

    @Operation(summary = "요양보호사 정보 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareworker(@PathVariable Long id) {
        careworkerService.deleteCareworker(id);
        return ResponseEntity.noContent().build();
    }
}
