package dbdr.security;

import dbdr.security.dto.BaseUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 JWT 토큰 추출
        String token = request.getHeader("Authorization");

        //유효한 토큰인지 확인
        if(token != null && jwtProvider.validateToken(token) && jwtProvider.isExpired(token)){
            //토큰에서 유저 정보 추출
            BaseUser baseUser = new BaseUser();
            baseUser.setUsername(jwtProvider.getUserName(token));
            baseUser.setRole(jwtProvider.getRole(token));

            BaseUserDetails userDetails = new BaseUserDetails(baseUser);


            //SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}
