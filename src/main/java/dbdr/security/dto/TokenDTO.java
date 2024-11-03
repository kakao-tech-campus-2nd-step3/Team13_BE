package dbdr.security.dto;

import lombok.Builder;

@Builder
public record TokenDTO(
        String refreshToken,
        String accessToken,
        String username
) {
}
