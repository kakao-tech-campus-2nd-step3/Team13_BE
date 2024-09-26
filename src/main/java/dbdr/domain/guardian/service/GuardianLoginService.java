package dbdr.domain.guardian.service;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.security.JwtProvider;
import dbdr.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianLoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtProvider jwtProvider;

    @Transactional
    public String login(GuardianRequest guardianRequest) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(guardianRequest.phone(), guardianRequest.loginPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtProvider.createToken(authentication.getName(), Role.GUARDIAN.name(), 1000L * 60 * 60 * 24 * 7);
    }


}
