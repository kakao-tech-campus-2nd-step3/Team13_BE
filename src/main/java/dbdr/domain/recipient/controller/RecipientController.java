package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@Tag(name = "[관리자] 돌봄대상자 (Recipient)", description = "돌봄대상자 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/recipient")
public class RecipientController {

    private final RecipientService recipientService;

    @Value("${spring.app.version}")
    private String appVersion;

    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @Operation(summary = "전체 돌봄대상자 정보 조회")
    @GetMapping
    public ResponseEntity<List<RecipientResponseDTO>> getAllRecipients() {
        List<RecipientResponseDTO> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "돌봄대상자 한 사람의 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(@PathVariable("id") Long id) {
        RecipientResponseDTO recipient = recipientService.getRecipientById(id);
        return ResponseEntity.ok(recipient);
    }

    @Operation(summary = "돌봄대상자 추가")
    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(@Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO newRecipient = recipientService.createRecipient(recipientDTO);
        return ResponseEntity.created(URI.create("/" + appVersion + "/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @Operation(summary = "돌봄대상자 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(@PathVariable("id") Long id, @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipient(id, recipientDTO);
        return ResponseEntity.ok(updatedRecipient);
    }

    @Operation(summary = "돌봄대상자 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable("id") Long id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.noContent().build();
    }
}
