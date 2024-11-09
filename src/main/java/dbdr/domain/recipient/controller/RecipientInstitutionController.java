package dbdr.domain.recipient.controller;

import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginInstitution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "[요양원] 돌봄대상자 관리", description = "요양원이 관리하는 모든 돌봄대상자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/institution/recipient")
@RequiredArgsConstructor
public class RecipientInstitutionController {

    private final RecipientService recipientService;

    @Operation(summary = "전체 돌봄대상자 조회 ", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<RecipientResponseDTO>>> getAllRecipients(
            @LoginInstitution Institution institution) {
        List<RecipientResponseDTO> recipients = recipientService.getRecipientsByInstitution(institution.getId());
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "돌봄대상자 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @LoginInstitution Institution institution) {
        RecipientResponseDTO recipient = recipientService.getRecipientByInstitution(recipientId, institution.getId());
        return ResponseEntity.ok(ApiUtils.success(recipient));
    }

    @Operation(summary = "돌봄대상자 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> createRecipient(
            @LoginInstitution Institution institution,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO newRecipient = recipientService.createRecipientForInstitution(recipientDTO, institution.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newRecipient));
    }

    @Operation(summary = "돌봄대상자 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginInstitution Institution institution,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipientForInstitution(recipientId, recipientDTO, institution.getId());
        return ResponseEntity.ok(ApiUtils.success(updatedRecipient));
    }

    @Operation(summary = "돌봄대상자 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginInstitution Institution institution) {
        recipientService.deleteRecipientForInstitution(recipientId, institution.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
