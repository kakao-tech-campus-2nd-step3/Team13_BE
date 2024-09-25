package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/${spring.app.version}/recipient")
public class RecipientController {

    private final RecipientService recipientService;

    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @GetMapping
    public ResponseEntity<List<RecipientResponseDTO>> getAllRecipients() {
        List<RecipientResponseDTO> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(recipients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(@PathVariable Long id) {
        RecipientResponseDTO recipient = recipientService.getRecipientById(id);
        return ResponseEntity.ok(recipient);
    }

    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(@Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO newRecipient = recipientService.createRecipient(recipientDTO);
        return ResponseEntity.created(URI.create("/${spring.app.version}/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(@PathVariable Long id, @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipient(id, recipientDTO);
        return ResponseEntity.ok(updatedRecipient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.noContent().build();
    }
}
