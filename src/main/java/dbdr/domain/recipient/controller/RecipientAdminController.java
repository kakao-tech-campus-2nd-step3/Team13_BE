package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.util.api.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "[관리자] 돌봄대상자 관리", description = "관리자가 관리하는 모든 돌봄대상자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/admin/recipient")
@RequiredArgsConstructor
public class RecipientAdminController {

    private final RecipientService recipientService;

    @Operation(summary = "전체 돌봄대상자 조회 ")
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<RecipientResponseDTO>>> getAllRecipients() {
        List<RecipientResponseDTO> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "돌봄대상자 정보 조회")
    @GetMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> getRecipientById(@PathVariable("recipientId") Long recipientId) {
        RecipientResponseDTO recipient = recipientService.getRecipientById(recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipient));
    }

    @Operation(summary = "돌봄대상자 추가")
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> createRecipient(@Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO newRecipient = recipientService.createRecipient(recipientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newRecipient));
    }

    @Operation(summary = "돌봄대상자 정보 수정")
    @PutMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipientForAdmin(recipientId, recipientDTO);
        return ResponseEntity.ok(ApiUtils.success(updatedRecipient));
    }

    @Operation(summary = "돌봄대상자 삭제")
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteRecipient(@PathVariable("recipientId") Long recipientId) {
        recipientService.deleteRecipient(recipientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
