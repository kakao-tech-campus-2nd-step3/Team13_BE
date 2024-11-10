package dbdr.security.service;


import dbdr.security.dto.LoginRequest;
import dbdr.security.dto.TokenDTO;
import dbdr.security.model.BaseUserDetails;
import dbdr.security.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class LoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final dbdr.security.model.JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginService(AuthenticationManagerBuilder authenticationManagerBuilder, dbdr.security.model.JwtProvider jwtProvider,
        PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TokenDTO login(Role role, LoginRequest loginRequest) {
        log.info("로그인 서비스 Null check, role: {}, loginRequest: {}", role, loginRequest);
        log.info("로그인 서비스 id : {}, password : {}", loginRequest.userId(), loginRequest.password());
        BaseUserDetails userDetails = BaseUserDetails.builder()
                .userLoginId(loginRequest.userId())
                .password(loginRequest.password())
                .role(role)
                .build();
        log.info("로그인 서비스 접근 시작, 아이디 : {}, 비밀번호 : {}", loginRequest.userId(), loginRequest.password());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                loginRequest.password());
        log.info("로그인 서비스 - SecurityContextHolder 진입 시작");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtProvider.createAllToken(authentication.getName(), role.name());
    }

    public TokenDTO renewAccessToken(String refreshToken) {
        return jwtProvider.renewTokens(refreshToken);
    }

    public void logout(String accessToken) {
        jwtProvider.deleteRefreshToken(accessToken);
    }

}
