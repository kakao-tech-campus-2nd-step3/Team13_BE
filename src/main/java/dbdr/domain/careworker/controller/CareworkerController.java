package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/careworker")
@RequiredArgsConstructor
public class CareworkerController {

    private final CareworkerService careworkerService;

    @Value("${spring.app.version}")
    private String appVersion;

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
                        URI.create("/" + appVersion + "/careworker/" + newCareworker.getId()))
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
