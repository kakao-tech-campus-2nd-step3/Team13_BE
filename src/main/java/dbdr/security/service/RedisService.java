package dbdr.security.service;

import dbdr.global.util.api.JwtUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh-token";
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String code, String refreshToken) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Duration duration = Duration.ofSeconds(JwtUtils.REFRESH_TOKEN_EXPIRATION_TIME);
        valueOperations.set(getRefreshCodeKey(code), refreshToken, duration);
    }

    public String getRefreshToken(String code) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return (String) valueOperations.get(getRefreshCodeKey(code));
    }

    public void deleteRefreshToken(String code) {
        redisTemplate.delete(getRefreshCodeKey(code));
    }

    private String getRefreshCodeKey(String code) {
        return REFRESH_TOKEN_PREFIX + code;
    }
}
