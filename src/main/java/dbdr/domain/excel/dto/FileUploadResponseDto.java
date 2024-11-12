package dbdr.domain.excel.dto;

import java.util.List;

public record FileUploadResponseDto(
        String storeName,
        List<FileDataResponseDto> uploadedData,
        List<FileDataResponseDto> failedData
) {}
