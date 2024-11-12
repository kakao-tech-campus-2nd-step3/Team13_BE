package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.dto.response.RecipientResponse;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
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

    @DbdrAuth(targetRole = Role.ADMIN)
    @Operation(summary = "전체 돌봄대상자 조회 ")
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<RecipientResponse>>> getAllRecipients() {
        List<RecipientResponse> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.RECIPIENT_ID, id="recipientId")
    @Operation(summary = "돌봄대상자 정보 조회")
    @GetMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> getRecipientById(@PathVariable("recipientId") Long recipientId) {
        RecipientResponse recipient = recipientService.getRecipientById(recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipient));
    }

    @DbdrAuth(targetRole = Role.ADMIN)
    @Operation(summary = "돌봄대상자 추가")
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> createRecipient(@Valid @RequestBody RecipientRequest recipientDTO) {
        RecipientResponse newRecipient = recipientService.createRecipient(recipientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newRecipient));
    }

    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.RECIPIENT_ID, id="recipientId")
    @Operation(summary = "돌봄대상자 정보 수정")
    @PutMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @Valid @RequestBody RecipientRequest recipientDTO
    ) {
        RecipientResponse updatedRecipient = recipientService.updateRecipientForAdmin(recipientId, recipientDTO);
        return ResponseEntity.ok(ApiUtils.success(updatedRecipient));
    }

    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.RECIPIENT_ID, id="recipientId")
    @Operation(summary = "돌봄대상자 삭제")
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteRecipient(@PathVariable("recipientId") Long recipientId) {
        recipientService.deleteRecipient(recipientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
