package dbdr.domain.excel.dto;

import java.util.List;

public record GuardianFileUploadResponseDto(
        String storeName,
        List<ExcelGuardianResponseDto> uploadedGuardians,
        List<ExcelGuardianResponseDto> failedGuardians
) {}