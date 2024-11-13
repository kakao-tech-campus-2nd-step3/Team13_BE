package dbdr.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class BusinessException extends RuntimeException {
    private final BusinessError businessError;

    public HttpStatus getHttpStatus() {
        return businessError.getStatus();
    }

    public String getErrorMessage() {
        return businessError.getMessage();
    }
}
