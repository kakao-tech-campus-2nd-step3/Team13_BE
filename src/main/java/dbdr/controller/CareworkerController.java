package dbdr.controller;

import dbdr.dto.request.CareworkerRequestDTO;
import dbdr.dto.response.CareworkerResponseDTO;
import dbdr.service.CareworkerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/careworkers")
public class CareworkerController {

    private final CareworkerService careworkerService;

    public CareworkerController(CareworkerService careworkerService) {
        this.careworkerService = careworkerService;
    }

    @GetMapping
    public ResponseEntity<List<CareworkerResponseDTO>> getAllCareworkers(
            @RequestParam(value = "institutionId", required = false) Long institutionId) {
        List<CareworkerResponseDTO> careworkers;
        if (institutionId != null) {
            careworkers = careworkerService.getCareworkersByInstitution(institutionId);
        } else {
            careworkers = careworkerService.getAllCareworkers();
        }
        return ResponseEntity.ok(careworkers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(@PathVariable Long id) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerById(id);
        return ResponseEntity.ok(careworker);
    }

    @PostMapping
    public ResponseEntity<CareworkerResponseDTO> createCareworker(@Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO);
        return ResponseEntity.created(URI.create("/v1/careworkers/" + newCareworker.getId()))
                .body(newCareworker);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(@PathVariable Long id, @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(id, careworkerDTO);
        return ResponseEntity.ok(updatedCareworker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareworker(@PathVariable Long id) {
        careworkerService.deleteCareworker(id);
        return ResponseEntity.noContent().build();
    }
}
