package dbdr.domain.excel.controller;

import dbdr.domain.excel.dto.FileUploadResponseDto;
import dbdr.domain.excel.service.ExcelDownloadService;
import dbdr.domain.excel.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/${spring.app.version}/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelDownloadService excelDownloadService;
    private final ExcelUploadService excelUploadService;

    @GetMapping("/careworker/download")
    public ResponseEntity<byte[]> downloadCareworkerTemplate() {
        byte[] data = excelDownloadService.generateCareworkerTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=careworker_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }

    @GetMapping("/guardian/download")
    public ResponseEntity<byte[]> downloadGuardianTemplate() {
        byte[] data = excelDownloadService.generateGuardianTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=guardian_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }

    @GetMapping("/recipient/download")
    public ResponseEntity<byte[]> downloadRecipientTemplate() {
        byte[] data = excelDownloadService.generateRecipientTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=recipient_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }

    @PostMapping("/careworker/upload")
    public ResponseEntity<FileUploadResponseDto> uploadCareworkerData(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto result = excelUploadService.uploadCareworkerExcel(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/guardian/upload")
    public ResponseEntity<FileUploadResponseDto> uploadGuardianData(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto result = excelUploadService.uploadGuardianExcel(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/recipient/upload")
    public ResponseEntity<FileUploadResponseDto> uploadRecipientData(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto result = excelUploadService.uploadRecipientExcel(file);
        return ResponseEntity.ok(result);
    }
}
