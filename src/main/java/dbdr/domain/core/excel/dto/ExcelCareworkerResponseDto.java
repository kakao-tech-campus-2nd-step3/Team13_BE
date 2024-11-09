package dbdr.domain.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelCareworkerResponseDto {

	private Long id;
	private Long institution;
	private String name;
	private String email;
	private String phone;
}
