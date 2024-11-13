package dbdr.domain.excel.dto;

import java.util.List;

public record RecipientFileUploadResponse(
        String storeName,
        List<ExcelRecipientResponse> uploadedRecipients,
        List<ExcelRecipientResponse> failedRecipients
) {}
