package dbdr.security.service;

import dbdr.security.model.JwtProvider;
import dbdr.security.model.Role;
import dbdr.security.model.BaseUserDetails;
import dbdr.security.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class LoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtProvider jwtProvider;

    private final Long jwtExpiration;

    public LoginService(AuthenticationManagerBuilder authenticationManagerBuilder, JwtProvider jwtProvider, @Value("${spring.jwt.jwtexpiration}") Long jwtExpiration) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtProvider = jwtProvider;
        this.jwtExpiration = jwtExpiration;
    }

    @Transactional
    public String login(Role role, LoginRequest loginRequest) {
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .userLoginId(loginRequest.userId())
            .password(loginRequest.password())
            .role(role.name())
            .build();
        log.debug("로그인 서비스 접근 시작");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, loginRequest.password());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtProvider.createAccessToken(authentication.getName(), role.name(), jwtExpiration);
    }

}
