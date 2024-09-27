package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/${spring.app.version}/careworker")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('CAREWORKER','ADMIN')")
public class CareworkerController {

    private final CareworkerService careworkerService;

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

    @GetMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
        @PathVariable Long id) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerById(id);
        return ResponseEntity.ok(careworker);
    }

    @PostMapping
    public ResponseEntity<CareworkerResponseDTO> createCareworker(
        @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO);
        return ResponseEntity.created(
                URI.create("/${spring.app.version}/careworker/" + newCareworker.getId()))
            .body(newCareworker);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(@PathVariable Long id,
        @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(id,
            careworkerDTO);
        return ResponseEntity.ok(updatedCareworker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareworker(@PathVariable Long id) {
        careworkerService.deleteCareworker(id);
        return ResponseEntity.noContent().build();
    }
}
