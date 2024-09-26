package dbdr.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BusinessError {

    LOGIN_FAILED("로그인에 실패했습니다."),
    PERMISSION_DENIED("권한이 없습니다."),
    CHART_CREATION_NOT_ALLOWED("차트 작성 가능한 시간이 아닙니다.");

    private final String message;
}
