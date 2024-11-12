package dbdr.security.model;

import static dbdr.global.exception.ApplicationError.REFRESH_TOKEN_EXPIRED;
import static dbdr.global.exception.ApplicationError.TOKEN_EXPIRED;
import static dbdr.global.util.api.JwtUtils.ACCESS_TOKEN_EXPIRATION_TIME;
import static dbdr.global.util.api.JwtUtils.REFRESH_TOKEN_EXPIRATION_TIME;
import static dbdr.global.util.api.JwtUtils.TOKEN_PREFIX;

import dbdr.global.exception.ApplicationException;
import dbdr.global.util.api.JwtUtils;
import dbdr.security.dto.TokenDTO;
import dbdr.security.service.BaseUserDetailsService;
import dbdr.security.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtProvider {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SecretKey secretKey;
    private final BaseUserDetailsService baseUserDetailsService;
    private final RedisService redisService;

    public JwtProvider(@Value("${spring.jwt.secret}") String secret,
                       BaseUserDetailsService baseUserDetailsService, RedisService redisService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        this.baseUserDetailsService = baseUserDetailsService;
        this.redisService = redisService;
    }

    public String extractToken(HttpServletRequest request) {
        log.info("request 토큰 추출 시작");
        String bearerToken = request.getHeader("Authorization");
        log.info("request 토큰 값 : {}", bearerToken);
        return removeTokenPrefix(bearerToken);
    }

    public String removeTokenPrefix(String token) {
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            log.info("제거 전 request 토큰 값 : {}", token);
            token = token.substring(TOKEN_PREFIX.length());
            log.info("request 토큰 값 : {}", token);
            return token;
        } else {
            return null;
        }
    }

    public String getUserName(String token) {
        return getJwtsBody(token).get("username", String.class);
    }

    public String getRole(String token) {
        return getJwtsBody(token).get("role", String.class);
    }

    public boolean isExpired(String token) {
        return getJwtsBody(token).getExpiration().before(new Date());
    }

    public TokenDTO createAllToken(String username, String role) {
        TokenDTO token = TokenDTO.builder()
                .refreshToken(createToken(username, role, REFRESH_TOKEN_EXPIRATION_TIME))
                .accessToken(createToken(username, role, ACCESS_TOKEN_EXPIRATION_TIME))
                .build();
        redisService.saveRefreshToken(role + username, token.refreshToken());
        return token;
    }

    private String createToken(String username, String role, Long expireTime) {
        return Jwts.builder().claim("username", username).claim("role", role)
                .setIssuer(JwtUtils.ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime * 1000))
                .signWith(secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        BaseUserDetails userDetails = baseUserDetailsService.loadUserByUsernameAndRole(getUserName(token),
                Role.valueOf(getRole(token)));
        validateBlackListToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    private void validateBlackListToken(String token) {
        if (!redisService.isBlackList(getRedisCode(token), token)) {
            log.info("get redis code : {}", getRedisCode(token));
            log.info("validate 실패");
            throw new ApplicationException(TOKEN_EXPIRED);
        }

    }

    public TokenDTO renewTokens(String refreshToken) {
        if (!isValidRedisRefreshToken(getRedisCode(refreshToken), refreshToken)) {

            log.info("renew 실패");
            throw new ApplicationException(REFRESH_TOKEN_EXPIRED);
        }
        redisService.deleteRefreshToken(getRedisCode(refreshToken));
        return createAllToken(getUserName(refreshToken), getRole(refreshToken));
    }

    public void deleteRefreshToken(String accessToken) {
        String token = removeTokenPrefix(accessToken);
        String redisCode = getRedisCode(token);
        redisService.deleteRefreshToken(redisCode);
        redisService.saveBlackList(redisCode, token);

    }

    private Boolean isValidRedisRefreshToken(String code, String refreshToken) {
        String token = redisService.getRefreshToken(code);
        if (token == null) {
            return false;
        }
        return token.equals(refreshToken);
    }

    private String getRedisCode(String token) {
        return getRole(token) + getUserName(token);
    }

    private Claims getJwtsBody(String token) {
        log.info("token : {}", token);
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
                .getBody();
    }
}
