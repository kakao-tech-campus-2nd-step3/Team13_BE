package dbdr.security.controller;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.Role;
import dbdr.security.dto.LoginRequest;
import dbdr.security.dto.TokenDTO;
import dbdr.security.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login/{role}")
    public ResponseEntity<TokenDTO> login(@PathVariable("role") String role,
                                          @RequestBody @Valid LoginRequest loginRequest) {
        Role roleEnum = roleCheck(role);
        TokenDTO token = loginService.login(roleEnum, loginRequest);
        return ResponseEntity.ok().header(authHeader, token.accessToken()).body(token);
    }

    @PostMapping("/renew")
    public ResponseEntity<TokenDTO> renewAccessToken(@RequestBody String refreshToken) {
        TokenDTO token = loginService.renewAccessToken(refreshToken);
        return ResponseEntity.ok().header(authHeader, token.accessToken()).body(token);
    }

    private Role roleCheck(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        }
    }
}
