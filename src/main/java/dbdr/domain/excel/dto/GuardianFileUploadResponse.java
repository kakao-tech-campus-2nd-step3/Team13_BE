package dbdr.domain.excel.dto;

import java.util.List;

public record GuardianFileUploadResponse(
        String storeName,
        List<ExcelGuardianResponse> uploadedGuardians,
        List<ExcelGuardianResponse> failedGuardians
) {}