package dbdr.security;

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
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            .getBody();
        return claims.get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody()
            .get("role", String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody()
            .getExpiration().before(new Date());
    }

    public String createToken(String username, String role, Long expireTime) {
        {
            return Jwts.builder().claim("username", username).claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey).compact();
        }
    }

    public Authentication getAuthentication(String token) {
        BaseUserDetails userDetails = baseUserDetailsService.loadUserByUsername(
            getUserName(token) + "_" + getRole(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }
}
