package dbdr.security.service;

import dbdr.exception.ApplicationError;
import dbdr.exception.ApplicationException;
import dbdr.security.dto.BaseUserDetails;
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

    public JwtProvider(@Value("${spring.jwt.secret}") String secret,
        BaseUserDetailsService baseUserDetailsService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.baseUserDetailsService = baseUserDetailsService;
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

    public String createToken(String username, String role, Long expireTime) {
        return Jwts.builder().claim("username", username).claim("role", role)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expireTime))
            .signWith(secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        BaseUserDetails userDetails = baseUserDetailsService.loadUserByUsername(
            getUserName(token) + "_" + getRole(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    public void validateToken(String token) {
        if(isExpired(token)) {
            throw new ApplicationException(ApplicationError.TOKEN_EXPIRED);
        }
    }

    private Claims getJwtsBody(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            .getBody();
    }
}
