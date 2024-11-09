package dbdr.domain.excel.dto;

import java.util.List;

public record CareworkerFileUploadResponseDto(
	String storeName,
	List<ExcelCareworkerResponseDto> uploadedCareworkers,
	List<ExcelCareworkerResponseDto> failedCareworkers
) {}
