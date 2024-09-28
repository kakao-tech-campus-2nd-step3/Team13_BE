package dbdr.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ApplicationException extends RuntimeException {
    private final ApplicationError applicationError;

    public HttpStatus getHttpStatus() {
        return applicationError.getStatus();
    }

    public String getErrorMessage() {
        return applicationError.getMessage();
    }
}
