package dbdr.global.exception;

import dbdr.global.util.api.ApiUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException exception) {
        List<ObjectError> objectErrors = exception.getBindingResult().getAllErrors();
        List<String> errors = new ArrayList<>();
        for (ObjectError objectError : objectErrors) {
            errors.add(objectError.getDefaultMessage());
        }
        String error = String.join("\n", errors);
        log.warn("{} : {}", exception.getCause(), exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtils.error(HttpStatus.BAD_REQUEST, error));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> applicationExceptionHandler(ApplicationException ex) {
        log.error("Application error: {}, ErrorCode: {}", ex.getMessage(), ex.getApplicationError());

        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiUtils.error(HttpStatus.BAD_REQUEST, ex.getApplicationError().getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> businessExceptionHandler(BusinessException ex) {
        log.error("Business error: {}, ErrorCode: {}", ex.getMessage(), ex.getBusinessError());

        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiUtils.error(HttpStatus.BAD_REQUEST, ex.getBusinessError().getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        log.error("잘못된 인자 값: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtils.error(HttpStatus.BAD_REQUEST, "잘못된 인자 값입니다."));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> nullPointerExceptionHandler(NullPointerException ex) {
        log.error("Null 참조 오류: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiUtils.error(HttpStatus.INTERNAL_SERVER_ERROR, "참조된 객체가 null입니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> httpRequestMethodNotSupportedExceptionHandler(
            HttpRequestMethodNotSupportedException ex) {
        log.error("지원되지 않는 HTTP 메서드: {}", ex.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiUtils.error(HttpStatus.METHOD_NOT_ALLOWED, "지원되지 않는 HTTP 메서드 입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiUtils.ApiResult<String>> generalExceptionHandler(Exception ex) {
        log.error("예상치 못한 오류 발생: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiUtils.error(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다."));
    }
}
