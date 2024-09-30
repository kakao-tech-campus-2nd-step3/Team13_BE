package dbdr.domain.institution.controller;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.service.InstitutionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/developer/institution")
@RequiredArgsConstructor
public class InstitutionDeveloperController {

    private final InstitutionService institutionService;

    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> showAllInstitution() {
        List<InstitutionResponse> institutionResponseList = institutionService.getAllInstitution();
        return ResponseEntity.ok(institutionResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> showOneInstitution(@PathVariable("id") Long id) {
        InstitutionResponse institutionResponse = institutionService.getInstitutionById(id);
        return ResponseEntity.ok(institutionResponse);
    }

    @PostMapping
    public ResponseEntity<InstitutionResponse> addInstitution(
        @Valid @RequestBody InstitutionRequest institutionRequest) {
        InstitutionResponse institutionResponse = institutionService.addInstitution(
            institutionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(institutionResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponse> updateInstitution(@PathVariable("id") Long id,
        @Valid @RequestBody InstitutionRequest institutionRequest) {
        InstitutionResponse institutionResponse = institutionService.updateInstitution(id,
            institutionRequest);
        return ResponseEntity.ok(institutionResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitution(@PathVariable("id") Long id) {
        institutionService.deleteInstitutionById(id);
        return ResponseEntity.noContent().build();
    }
}
