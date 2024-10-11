package dbdr.security.model;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.service.BaseUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Slf4j
public class BaseAuthenticationProvider implements AuthenticationProvider {

    private final BaseUserDetailsService baseUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        BaseUserDetails unAuthUser = (BaseUserDetails) authentication.getPrincipal();
        BaseUserDetails authUser = baseUserDetailsService.loadUserByUsernameAndRole(unAuthUser.getUserLoginId(), unAuthUser.getRole());

        if (!passwordEncoder.matches(unAuthUser.getPassword(), authUser.getPassword())) {
            throw new ApplicationException(ApplicationError.PASSWORD_NOT_MATCH);
        }
        return new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("supports : {}", authentication.equals(UsernamePasswordAuthenticationToken.class));
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
