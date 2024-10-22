package dbdr.security.service;

import static dbdr.global.util.api.JwtUtils.ACCESS_TOKEN_EXPIRATION_TIME;
import static dbdr.global.util.api.JwtUtils.REFRESH_TOKEN_EXPIRATION_TIME;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.global.util.api.JwtUtils;
import dbdr.security.Role;
import dbdr.security.dto.BaseUserDetails;
import dbdr.security.dto.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final BaseUserDetailsService baseUserDetailsService;
    private final RedisService redisService;

    public JwtProvider(@Value("${spring.jwt.secret}") String secret,
                       BaseUserDetailsService baseUserDetailsService, RedisService redisService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.baseUserDetailsService = baseUserDetailsService;
        this.redisService = redisService;
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
        redisService.saveRefreshToken(username, token.refreshToken());
        return token;
    }

    public String createToken(String username, String role, Long expireTime) {
        return Jwts.builder().claim("username", username).claim("role", role)
                .setIssuer(JwtUtils.ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        BaseUserDetails userDetails = baseUserDetailsService.loadUserByUsernameAndRole(
                getUserName(token), Role.valueOf(getRole(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    public void validateToken(String token) {
        if (isExpired(token)) {
            throw new ApplicationException(ApplicationError.TOKEN_EXPIRED);
        }
    }

    private Claims getJwtsBody(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
                .getBody();
    }
}
