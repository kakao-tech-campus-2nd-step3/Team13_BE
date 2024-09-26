package dbdr.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationError {

    //Auth
    ROLE_NOT_FOUND("해당 유저가 ROLE을 가지고 있지 않습니다."),
    ACCESS_NOT_ALLOWED("접근 권한이 없습니다."),

    // Guardian (보호자)
    GUARDIAN_NOT_FOUND("해당 보호자를 찾을 수가 없습니다."),

    // Careworker (요양보호사)
    CAREWORKER_NOT_FOUND("해당 요양보호사를 찾을 수가 없습니다."),

    // Recipient (돌봄대상자)
    RECIPIENT_NOT_FOUND("해당 돌봄대상자를 찾을 수가 없습니다."),

    // 공통
    DUPLICATE_EMAIL("이미 존재하는 이메일입니다."),
    DUPLICATE_PHONE("이미 존재하는 전화번호입니다."),
    DUPLICATE_CARE_NUMBER("이미 존재하는 장기요양번호입니다."),
    INVALID_INPUT("잘못된 입력값입니다."),

    // 시스템
    DATABASE_ERROR("데이터베이스 처리 중 오류가 발생했습니다.");

    private final String message;
}
