package dbdr.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 JWT 토큰 추출
        String token = request.getHeader("Authorization");

        //유효한 토큰인지 확인
        if(token != null){ //TODO 유효성 검사 필요
            try {
                //토큰에서 유저 정보 추출
                Authentication authentication = jwtProvider.getAuthentication(token);

                //SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.debug("토큰 유저 정보 추출 실패 : {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

}
