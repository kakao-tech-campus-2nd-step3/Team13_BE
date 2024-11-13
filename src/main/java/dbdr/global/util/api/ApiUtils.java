package dbdr.global.util.api;

import org.springframework.http.HttpStatus;

public final class ApiUtils {

    private ApiUtils() {
    }

    // 성공 응답 (response가 있을 때)
    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    // 성공 응답 (response가 없을 때)
    public static ApiResult<String> success() {
        return new ApiResult<>(true, "요청이 성공적으로 처리되었습니다.", null);
    }

    // 에러 응답
    public static <T> ApiResult<T> error(HttpStatus status, String message) {
        return new ApiResult<>(false, null, new ApiError(status.value(), message));
    }

    // ApiResult 기록 (success, response, error)
    public record ApiResult<T>(boolean success, T response, ApiError error) {
    }

    // ApiError 기록 (status, message)
    public record ApiError<T>(int status, T message) {
    }
}
