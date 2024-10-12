package dbdr.global.exception;

import org.springframework.web.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        List<ObjectError> objectErrors = exception.getBindingResult().getAllErrors();
        List<String> errors = new ArrayList<>();
        for (ObjectError objectError : objectErrors) {
            errors.add(objectError.getDefaultMessage());
        }
        String error = String.join("\n", errors);
        log.warn("{} : {}", exception.getCause(), exception.getMessage(), exception);

        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, error)
                .title("Validation Failed")
                .build();
    }


    @ExceptionHandler(ApplicationException.class)
    public ErrorResponse applicationExceptionHandler(ApplicationException ex) {
        log.error("Application error: {}, ErrorCode: {}", ex.getMessage(), ex.getApplicationError());

        return ErrorResponse.builder(ex, ex.getHttpStatus(), ex.getApplicationError().getMessage())
                .title(ex.getApplicationError().name())
                .build();
    }

    @ExceptionHandler(BusinessException.class)
    public ErrorResponse businessExceptionHandler(BusinessException ex) {
        log.error("Business error: {}, ErrorCode: {}", ex.getMessage(), ex.getBusinessError());

        return ErrorResponse.builder(ex, ex.getHttpStatus(), ex.getBusinessError().getMessage())
                .title(ex.getBusinessError().name())
                .build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        log.error("잘못된 인자 값: {}", ex.getMessage());

        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage())
                .title("Invalid Argument")
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ErrorResponse nullPointerExceptionHandler(NullPointerException ex) {
        log.error("Null 참조 오류: {}", ex.getMessage());

        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "참조된 객체가 null입니다.")
                .title("Null Pointer Exception")
                .build();
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.error("지원되지 않는 HTTP 메서드: {}", ex.getMethod());

        return ErrorResponse.builder(ex, HttpStatus.METHOD_NOT_ALLOWED, "요청한 메서드: " + ex.getMethod())
                .title("Method Not Allowed")
                .build();
    }


    @ExceptionHandler(Exception.class)
    public ErrorResponse generalExceptionHandler(Exception ex) {
        log.error("예상치 못한 오류 발생: {}", ex.getMessage(), ex);

        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다.")
                .title("Internal Server Error")
                .build();
    }
}

