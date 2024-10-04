package dbdr.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum BusinessError {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    CHART_CREATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "차트 작성 가능한 시간이 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
