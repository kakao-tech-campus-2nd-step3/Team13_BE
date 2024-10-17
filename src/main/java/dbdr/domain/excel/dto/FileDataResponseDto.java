package dbdr.domain.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileDataResponseDto {

    private String name;
    private String phone;
    private String careRecipientName;
    private String careNumber;
    private String birth;

    // 요양보호사용 생성자
    public FileDataResponseDto(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.careRecipientName = null;
        this.careNumber = null;
        this.birth = null;
    }

    // 보호자용 생성자
    public FileDataResponseDto(String name, String phone, String careRecipientName, String careNumber) {
        this.name = name;
        this.phone = phone;
        this.careRecipientName = careRecipientName;
        this.careNumber = careNumber;
        this.birth = null;
    }

    // 돌봄대상자용 생성자
    public FileDataResponseDto(String name, String careNumber, String birth) {
        this.name = name;
        this.phone = null;
        this.careRecipientName = null;
        this.careNumber = careNumber;
        this.birth = birth;
    }
}
