package dbdr.domain.excel.dto;

import java.util.List;

public record CareworkerFileUploadResponse(
        String storeName,
        List<ExcelCareworkerResponse> uploadedCareworkers,
        List<ExcelCareworkerResponse> failedCareworkers
) {}
