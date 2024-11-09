package dbdr.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ApplicationError {

    //Auth
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저가 ROLE을 가지고 있지 않습니다."),
    ACCESS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 재로그인 해주세요."),

    // Guardian (보호자)
    GUARDIAN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 보호자를 찾을 수가 없습니다."),

    // Careworker (요양보호사)
    CAREWORKER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 요양보호사를 찾을 수가 없습니다."),

    // Recipient (돌봄대상자)
    RECIPIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 돌봄대상자를 찾을 수가 없습니다."),

    // Institution (요양원)
    INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 요양원을 찾을 수가 없습니다."),

    // 공통
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다."),
    DUPLICATE_CARE_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 장기요양번호입니다."),
    DUPLICATE_INSTITUTION_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 요양기관번호입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "잘못된 입력입니다. 010XXXXXXXX 형식으로 입력해주세요."),

    // 시스템
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),

    //JSON
    JSON_PARSING_ERROR(HttpStatus.BAD_REQUEST, "잘못된 json형식입니다."),

    //Date
    CANNOT_DETECT_DATE(HttpStatus.BAD_REQUEST, "알 수 없는 날짜입니다."),

    // Line Message API
    CANNOT_FIND_EVENT(HttpStatus.NOT_FOUND, "해당 이벤트를 찾을 수가 없습니다."),
    EVENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이벤트 처리 중 오류가 발생했습니다."),
    EVENT_ARRAY_NOT_FOUND(HttpStatus.NOT_FOUND, "events 배열을 찾을 수가 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수가 없습니다."),
    MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다."),
    FAILED_TO_GET_USER_PROFILE(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 프로필을 가져오는데 실패했습니다."),

    //엑셀 파일
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    FILE_DOWNLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 중 오류가 발생했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기는 5MB를 초과할 수 없습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "유효하지 않은 파일입니다. 엑셀 파일 (.xlsx)만 업로드 가능합니다.");

    private final HttpStatus status;
    private final String message;
}
