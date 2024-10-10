package dbdr.security.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import dbdr.security.service.BaseUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    JwtProvider jwtProvider;

    @Mock
    BaseUserDetailsService baseUserDetailsService;

    String secret = "testsecretabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(secret, baseUserDetailsService);
    }

    @Test
    void 토큰_생성하기(){
        // given
        String username = "asdf";
        String role = "testRole";
        Long expireTime = 1000L;

        //when
        String token = jwtProvider.createToken(username, role, expireTime);

        //then
        assertThat(token).isNotNull();
    }

    @Test
    void 토큰_유저이름_추출하기(){
        // given
        String username = "asdf";
        String role = "testRole";
        Long expireTime = 1000L;
        String token = jwtProvider.createToken(username, role, expireTime);

        //when
        String result = jwtProvider.getUserName(token);

        //then
        assertThat(result).isEqualTo(username);
    }

    @Test
    void 토큰_역할_추출하기(){
        // given
        String username = "asdf";
        String role = "testRole";
        Long expireTime = 1000L;
        String token = jwtProvider.createToken(username, role, expireTime);

        //when
        String result = jwtProvider.getRole(token);

        //then
        assertThat(result).isEqualTo(role);
    }

    @Test
    void 토큰_만료여부_확인하기(){
        // given
        String username = "asdf";
        String role = "testRole";
        Long expireTime = 1000L;
        String token = jwtProvider.createToken(username, role, expireTime);

        //when
        boolean result = jwtProvider.isExpired(token);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 토큰에서_인증_정보_추출하기(){
        // given
        String username = "asdf";
        String role = "ADMIN";
        Long expireTime = 10000L;
        String token = jwtProvider.createToken(username, role, expireTime);

        Mockito.doAnswer(invocation -> {
            String username1 = invocation.getArgument(0);
            Role role1 = invocation.getArgument(1);
            return BaseUserDetails.builder()
                .userLoginId(username1)
                .role(role1.name())
                .password("test")
                .institutionId(1L)
                .build();
        }).when(baseUserDetailsService).loadUserByUsernameAndRole(username, Role.valueOf(role));

        //when
        Authentication authentication = jwtProvider.getAuthentication(token);

        //then
        assertThat(authentication.getName()).isEqualTo(username);
        assertThat(authentication.getAuthorities().size()).isEqualTo(1);
        assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo(role);
    }
}