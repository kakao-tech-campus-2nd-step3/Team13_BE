package dbdr.domain.recipient.controller;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.recipient.dto.response.RecipientResponse;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginGuardian;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @DbdrAuth(targetRole = Role.GUARDIAN)
    @Operation(summary = "보호자가 확인 가능한 모든 돌봄대상자 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<RecipientResponse>>> getAllRecipients(
            @Parameter(hidden = true) @LoginGuardian Guardian guardian) {
        List<RecipientResponse> recipients = recipientService.getAllRecipientsForGuardian(guardian.getId());
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @DbdrAuth(targetRole = Role.GUARDIAN)
    @Operation(summary = "보호자가 확인 가능한 특정 돌봄대상자 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @Parameter(hidden = true) @LoginGuardian Guardian guardian) {
        RecipientResponse recipient = recipientService.getRecipientForGuardian(guardian.getId(), recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipient));
    }
}
