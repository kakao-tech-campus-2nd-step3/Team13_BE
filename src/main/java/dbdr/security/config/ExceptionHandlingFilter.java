package dbdr.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dbdr.global.util.api.ApiUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(request, response, ex);
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex)
            throws IOException {
        ApiUtils.ApiResult<?> apiResult;
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        if (ex instanceof ExpiredJwtException) {
            apiResult = ApiUtils.error(status, "토큰이 만료되었습니다.");
        } else if (ex instanceof UnsupportedJwtException) {
            apiResult = ApiUtils.error(status, "지원하지 않는 토큰 형식입니다.");
        } else if (ex instanceof MalformedJwtException) {
            apiResult = ApiUtils.error(status, "토큰의 형식이 올바르지 않습니다.");
        } else if (ex instanceof SignatureException || ex instanceof SecurityException) {
            apiResult = ApiUtils.error(status, "토큰의 서명이 유효하지 않습니다.");
        } else if (ex instanceof IllegalArgumentException) {
            apiResult = ApiUtils.error(status, "토큰이 제공되지 않았습니다.");
        } else if (ex instanceof JwtException) {
            apiResult = ApiUtils.error(status, "유효하지 않은 토큰입니다.");
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            apiResult = ApiUtils.error(status, "서버 오류가 발생했습니다.");
        }

        log.error("Security Exception 발생: [{}] {}", request.getRequestURI(), ex.getMessage());

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(apiResult);
        response.getWriter().write(jsonResponse);
    }
}
