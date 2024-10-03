package dbdr.domain.excel.controller;

import dbdr.domain.excel.dto.FileUploadResponseDto;
import dbdr.domain.excel.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/${spring.app.version}/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;


    @GetMapping("/careworker/download")
    public ResponseEntity<byte[]> downloadCareworkerTemplate() {
        byte[] data = excelService.generateCareworkerTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=careworker_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }


    @GetMapping("/guardian/download")
    public ResponseEntity<byte[]> downloadGuardianTemplate() {
        byte[] data = excelService.generateGuardianTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=guardian_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }


    @GetMapping("/recipient/download")
    public ResponseEntity<byte[]> downloadRecipientTemplate() {
        byte[] data = excelService.generateRecipientTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=recipient_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }


    @PostMapping("/careworker/upload")
    public ResponseEntity<FileUploadResponseDto> uploadCareworkerData(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto result = excelService.uploadCareworkerExcel(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/guardian/upload")
    public ResponseEntity<FileUploadResponseDto> uploadGuardianData(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto result = excelService.uploadGuardianExcel(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/recipient/upload")
    public ResponseEntity<FileUploadResponseDto> uploadRecipientData(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto result = excelService.uploadRecipientExcel(file);
        return ResponseEntity.ok(result);
    }
}