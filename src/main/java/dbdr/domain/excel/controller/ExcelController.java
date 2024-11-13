package dbdr.domain.excel.controller;

import dbdr.domain.excel.dto.CareworkerFileUploadResponse;
import dbdr.domain.excel.dto.GuardianFileUploadResponse;
import dbdr.domain.excel.dto.RecipientFileUploadResponse;
import dbdr.domain.excel.service.ExcelDownloadService;
import dbdr.domain.excel.service.ExcelUploadService;
import dbdr.domain.institution.entity.Institution;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.LoginInstitution;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "엑셀 양식 다운로드 및 업로드", description = "요양 관리사, 보호자, 돌봄 대상자 정보를 엑셀 파일을 통해 대량으로 업로드할 수 있습니다.")
@RestController
@RequestMapping("/${spring.app.version}/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelDownloadService excelDownloadService;
    private final ExcelUploadService excelUploadService;

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApplicationException(ApplicationError.EMPTY_FILE);
        }
        if (!EXCEL_CONTENT_TYPE.equals(file.getContentType())) {
            throw new ApplicationException(ApplicationError.INVALID_FILE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApplicationException(ApplicationError.FILE_SIZE_EXCEEDED);
        }
    }

    @Operation(summary = "요양보호사 엑셀 다운로드")
    @GetMapping("/careworker/download")
    public ResponseEntity<byte[]> downloadCareworkerTemplate() {
        byte[] data = excelDownloadService.generateCareworkerTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=careworker_template.xlsx")
                .header("Content-Type", EXCEL_CONTENT_TYPE)
                .body(data);
    }

    @Operation(summary = "보호자 엑셀 다운로드")
    @GetMapping("/guardian/download")
    public ResponseEntity<byte[]> downloadGuardianTemplate() {
        byte[] data = excelDownloadService.generateGuardianTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=guardian_template.xlsx")
                .header("Content-Type", EXCEL_CONTENT_TYPE)
                .body(data);
    }


    @Operation(summary = "돌봄대상자 엑셀 다운로드")
    @GetMapping("/recipient/download")
    public ResponseEntity<byte[]> downloadRecipientTemplate() {
        byte[] data = excelDownloadService.generateRecipientTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=recipient_template.xlsx")
                .header("Content-Type", EXCEL_CONTENT_TYPE)
                .body(data);
    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.LOGIN_INSTITUTION)
    @Operation(summary = "요양보호사 엑셀 업로드")
    @PostMapping("/careworker/upload")
    public ResponseEntity<CareworkerFileUploadResponse> uploadCareworkerData(
            @Parameter(hidden = true) @LoginInstitution Institution institution,
            @RequestParam("file") MultipartFile file) {
        validateFile(file);
        CareworkerFileUploadResponse result = excelUploadService.uploadCareworkerExcel(file, institution.getId());
        return ResponseEntity.ok(result);
    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.LOGIN_INSTITUTION)
    @Operation(summary = "보호자 엑셀 업로드")
    @PostMapping("/guardian/upload")
    public ResponseEntity<GuardianFileUploadResponse> uploadGuardianData(
            @Parameter(hidden = true) @LoginInstitution Institution institution,
            @RequestParam("file") MultipartFile file) {
        validateFile(file);
        GuardianFileUploadResponse result = excelUploadService.uploadGuardianExcel(file, institution.getId());
        return ResponseEntity.ok(result);
    }

    @DbdrAuth(targetRole = Role.INSTITUTION, authParam = AuthParam.LOGIN_INSTITUTION)
    @Operation(summary = "돌봄대상자 엑셀 업로드")
    @PostMapping("/recipient/upload")
    public ResponseEntity<RecipientFileUploadResponse> uploadRecipientData(
            @Parameter(hidden = true) @LoginInstitution Institution institution,
            @RequestParam("file") MultipartFile file) {
        validateFile(file);
        RecipientFileUploadResponse result = excelUploadService.uploadRecipientExcel(file, institution.getId());
        return ResponseEntity.ok(result);
    }
}
