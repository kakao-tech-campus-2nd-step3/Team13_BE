package dbdr.domain.recipient.controller;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginGuardian;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[보호자] 돌봄대상자 조회", description = "보호자가 확인 가능한 돌봄대상자 정보 조회")
@RestController
@RequestMapping("/${spring.app.version}/guardian/recipient")
@RequiredArgsConstructor
public class RecipientGuardianController {

    private final RecipientService recipientService;

    @Operation(summary = "보호자가 확인 가능한 모든 돌봄대상자 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<RecipientResponseDTO>>> getAllRecipients(
            @LoginGuardian Guardian guardian) {
        List<RecipientResponseDTO> recipients = recipientService.getAllRecipientsForGuardian(guardian.getId());
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "보호자가 확인 가능한 특정 돌봄대상자 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponseDTO>> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @LoginGuardian Guardian guardian) {
        RecipientResponseDTO recipient = recipientService.getRecipientForGuardian(guardian.getId(), recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipient));
    }
}

