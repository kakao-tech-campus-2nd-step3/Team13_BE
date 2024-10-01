package dbdr.security.service;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.security.Role;
import dbdr.security.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.userId(), loginRequest.password());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtProvider.createToken(loginRequest.userId(), role.name(), jwtExpiration);
    }

}
