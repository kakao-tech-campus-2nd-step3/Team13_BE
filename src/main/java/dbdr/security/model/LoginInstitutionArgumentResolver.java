package dbdr.security.model;

import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.LoginInstitution;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginInstitutionArgumentResolver implements HandlerMethodArgumentResolver {

    private final InstitutionRepository institutionRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginInstitution.class) != null &&
            Institution.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        BaseUserDetails baseUserDetails = (BaseUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return institutionRepository.findById(baseUserDetails.getId())
            .orElseThrow(
                () -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

    }

}
