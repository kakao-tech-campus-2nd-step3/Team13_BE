package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "전체 요양보호사 정보를 특정 요양원아이디로 조회")
    @GetMapping
    public ResponseEntity<List<CareworkerResponseDTO>> getAllCareworkers(
            @RequestParam(value = "institutionId", required = false) Long institutionId) {
        List<CareworkerResponseDTO> careworkerList;
        if (institutionId != null) {
            careworkerList = careworkerService.getCareworkersByInstitution(institutionId);
        } else {
            careworkerList = careworkerService.getAllCareworkers();
        }
        return ResponseEntity.ok(careworkerList);
    }

    @Operation(summary = "요양보호사 한 사람의 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
        @PathVariable("id") Long id) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerById(id);
        return ResponseEntity.ok(careworker);
    }

    @Operation(summary = "요양보호사 추가")
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
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(@PathVariable("id") Long id,
        @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(id,
                careworkerDTO);
        return ResponseEntity.ok(updatedCareworker);
    }

    @Operation(summary = "요양보호사 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareworker(@PathVariable("id") Long id) {
        careworkerService.deleteCareworker(id);
        return ResponseEntity.noContent().build();
    }
}
