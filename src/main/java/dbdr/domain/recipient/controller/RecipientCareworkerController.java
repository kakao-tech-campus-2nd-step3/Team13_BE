package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.dto.response.RecipientResponse;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginCareworker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "[요양보호사] 돌봄대상자 관리", description = "요양보호사가 담당하는 돌봄대상자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/careworker/recipient")
@RequiredArgsConstructor
public class RecipientCareworkerController {

    private final RecipientService recipientService;

    @Operation(summary = "담당 돌봄대상자 전체 조회 ", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<RecipientResponse>>> getAllRecipients(
            @LoginCareworker Careworker careworker) {
        List<RecipientResponse> recipients = recipientService.getRecipientsByCareworker(careworker.getId());
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "담당 돌봄대상자 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @LoginCareworker Careworker careworker) {
        RecipientResponse recipient = recipientService.getRecipientByCareworker(recipientId, careworker.getId());
        return ResponseEntity.ok(ApiUtils.success(recipient));
    }

    @Operation(summary = "담당 돌봄대상자 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> createRecipient(
            @Valid @RequestBody RecipientRequest recipientDTO,
            @LoginCareworker Careworker careworker) {
        RecipientResponse newRecipient = recipientService.createRecipientForCareworker(recipientDTO, careworker.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newRecipient));
    }

    @Operation(summary = "담당 돌봄대상자 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<RecipientResponse>> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginCareworker Careworker careworker,
            @Valid @RequestBody RecipientRequest recipientDTO) {
        RecipientResponse updatedRecipient = recipientService.updateRecipientForCareworker(recipientId, recipientDTO, careworker.getId());
        return ResponseEntity.ok(ApiUtils.success(updatedRecipient));
    }

    @Operation(summary = "담당 돌봄대상자 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginCareworker Careworker careworker) {
        recipientService.deleteRecipientForCareworker(recipientId, careworker.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}