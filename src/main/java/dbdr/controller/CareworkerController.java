package dbdr.controller;

import dbdr.dto.CareworkerDTO;
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
    public ResponseEntity<List<CareworkerDTO>> getAllCareworkers(
            @RequestParam(value = "institutionId", required = false) Long institutionId) {
        List<CareworkerDTO> careworkers;
        if (institutionId != null) {
            careworkers = careworkerService.getCareworkersByInstitution(institutionId);
        } else {
            careworkers = careworkerService.getAllCareworkers();
        }
        return ResponseEntity.ok(careworkers);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CareworkerDTO> getCareworkerById(@PathVariable Long id) {
        CareworkerDTO careworker = careworkerService.getCareworkerById(id);
        return ResponseEntity.ok(careworker);
    }

    @PostMapping
    public ResponseEntity<CareworkerDTO> createCareworker(@Valid @RequestBody CareworkerDTO careworkerDTO) {
        CareworkerDTO newCareworker = careworkerService.createCareworker(careworkerDTO);
        return ResponseEntity.created(URI.create("/v1/careworkers/" + newCareworker.getId()))
                .body(newCareworker);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareworkerDTO> updateCareworker(@PathVariable Long id, @Valid @RequestBody CareworkerDTO careworkerDTO) {
        CareworkerDTO updatedCareworker = careworkerService.updateCareworker(id, careworkerDTO);
        return ResponseEntity.ok(updatedCareworker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareworker(@PathVariable Long id) {
        careworkerService.deleteCareworker(id);
        return ResponseEntity.noContent().build();
    }
}