package dbdr.security.controller;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.Role;
import dbdr.security.dto.LoginRequest;
import dbdr.security.dto.TokenDTO;
import dbdr.security.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인, 로그아웃", description = "로그인과 로그아웃, 토큰 리프레시")
@RestController
@RequestMapping("/${spring.app.version}/auth")
public class LoginController {

    private final LoginService loginService;
    private final String authHeader;

    public LoginController(LoginService loginService,
                           @Value("${spring.jwt.authheader}") String authHeader) {
        this.loginService = loginService;
        this.authHeader = authHeader;
    }

    @Operation(summary = "해당 역할로 로그인")
    @PostMapping("/login/{role}")
    public ResponseEntity<TokenDTO> login(@PathVariable("role") String role,
                                          @RequestBody @Valid LoginRequest loginRequest) {
        Role roleEnum = roleCheck(role);
        TokenDTO token = loginService.login(roleEnum, loginRequest);
        return ResponseEntity.ok().header(authHeader, token.accessToken()).body(token);
    }

    @Operation(summary = "리프레시 토큰으로 액세스 토큰 재발급")
    @PostMapping("/renew")
    public ResponseEntity<TokenDTO> renewAccessToken(@RequestBody String refreshToken) {
        TokenDTO token = loginService.renewAccessToken(refreshToken);
        return ResponseEntity.ok().header(authHeader, token.accessToken()).body(token);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        loginService.logout(accessToken);
        return ResponseEntity.ok().build();
    }

    private Role roleCheck(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        }
    }
}
