package dbdr.security.config;

import dbdr.security.service.LoginCareworkerArgumentResolver;
import dbdr.security.service.LoginGuardianArgumentResolver;
import dbdr.security.service.LoginInstitutionArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginDbdrUserConfig implements WebMvcConfigurer {

    private final LoginGuardianArgumentResolver loginGuardianArgumentResolver;
    private final LoginInstitutionArgumentResolver loginInstitutionArgumentResolver;
    private final LoginCareworkerArgumentResolver loginCareworkerArgumentResolver;

    public LoginDbdrUserConfig(LoginGuardianArgumentResolver loginGuardianArgumentResolver, LoginInstitutionArgumentResolver loginInstitutionArgumentResolver, LoginCareworkerArgumentResolver loginCareworkerArgumentResolver) {
        this.loginGuardianArgumentResolver = loginGuardianArgumentResolver;
        this.loginInstitutionArgumentResolver = loginInstitutionArgumentResolver;
        this.loginCareworkerArgumentResolver = loginCareworkerArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginGuardianArgumentResolver);
        resolvers.add(loginInstitutionArgumentResolver);
        resolvers.add(loginCareworkerArgumentResolver);
    }

}
