package dbdr.domain.institution.controller;

import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.institution.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${spring.app.version}/institution")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> showInstitutionInfo(@PathVariable("id") Long id) {
        InstitutionResponse institutionResponse = institutionService.getInstitutionById(id);
        return ResponseEntity.ok(institutionResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponse> updateInstitutionById(@PathVariable("id") Long id,
        @Valid @RequestBody InstitutionRequest institutionRequest) {
        InstitutionResponse institutionResponse = institutionService.updateInstitution(id,
            institutionRequest);
        return ResponseEntity.ok(institutionResponse);
    }
}
