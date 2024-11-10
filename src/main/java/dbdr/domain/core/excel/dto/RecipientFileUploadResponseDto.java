package dbdr.domain.excel.dto;

import java.util.List;

public record RecipientFileUploadResponseDto(
        String storeName,
        List<ExcelRecipientResponseDto> uploadedRecipients,
        List<ExcelRecipientResponseDto> failedRecipients
) {}