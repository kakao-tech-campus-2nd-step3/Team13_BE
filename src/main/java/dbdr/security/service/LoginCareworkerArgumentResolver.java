package dbdr.security.service;

import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.LoginCareworker;
import dbdr.security.dto.BaseUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginCareworkerArgumentResolver implements HandlerMethodArgumentResolver {

    private final CareworkerRepository careworkerRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginCareworker.class) != null &&
            UserDetails.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        BaseUserDetails baseUserDetails = (BaseUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return careworkerRepository.findById(baseUserDetails.getId())
            .orElseThrow(
                () -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

    }
}
