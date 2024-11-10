package dbdr.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RenewTokenRequest(
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjI5MjUwNzYwLCJleHAiOjE2MjkzNDA3NjB9.7")
        String refreshToken
) {
}
