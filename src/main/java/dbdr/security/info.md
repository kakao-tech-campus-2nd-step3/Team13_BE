# 시큐리티 flow 정리

## 1. 사용자 로그인 시도 시

1. 사용자의 로그인은 SecurityConfig에서 ```authorize.requestMatchers("/*/login/*").permitAll()``` 에 의해
컨트롤러로 인증없이 통과하게 됩니다.

2. LoginController에서 LoginService로 로그인 시도를 위임합니다.

3. LoginService에서는 UserDetails을 구현한 BaseUserDetails 요청자의 request정보를 이용해 생성하고
이를 이용해 UsernamePasswordAuthenticationToken을 생성합니다.

4. UsernamePasswordAuthenticationToken은 스프링 시큐리티에서 제공하는 인증객체로 사용자의 아이디와 비밀번호를 캡슐화하여
AuthenticationManager에게 전달합니다.

5. AuthenticationManagerBuilder를 통해 생성된 AuthenticationManager는 인증을 위임받은 ProviderManager에게
해당 인증요청을 보내게 되는데, 이떄 ProviderManager는 BaseAuthenticationProvider에게 인증을 위임합니다.

(여기서 BaseAuthenticationProvider는 AuthenticationProvider를 구현한 클래스입니다. SecurityConfig를 보면 해당 객체를
등록하고 있는 모습 ```http.authenticationProvider(baseAuthenticationProvider())```을 볼 수 있어요!!)

6. 따라서 실질적인 인증은 BaseAuthenticationProvider에서 이루어지게 되는데, 이때 BaseAuthenticationProvider는
전달받은 객체를 이용해서 실제 DB를 조회 후 인증을 진행하고 인증 성공 시 UsernamePasswordAuthenticationToken을 반환합니다.

7. 인증이 성공하면, 다시 컨트롤러로 넘어와서 JWT 토큰을 생성하여 클라이언트에게 반환합니다.

## 2. 사용자의 권한확인

1. 사용자의 권한확인은 SecurityConfig에서 ```http.anyRequest().authenticated();``` 에 의해 동작하게 됩니다.

2. ```.addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)``` 에 의해
JwtFilter가 UsernamePasswordAuthenticationFilter 앞에 위치하게 되어, 사용자의 요청이 들어오면 JwtFilter에서
JWT 토큰을 확인하고 이를 이용해서 사용자를 확인하게 됩니다.

3. 해당 필터에서는 jwt 토큰을 디코딩하여 사용자 정보를 BaseUserDetailsService에 넘겨 확인하게 됩니다.

4. BaseUserDetailsService에서는 사용자의 정보를 활용해서 DB에 조회 후 성공한다면 
```private BaseUserDetails securityRegister(Long id, String username, String password, Role role)```
메소드에서 securityContextHolder에 사용자 정보를 등록하게 됩니다.

---
## 3. 왜 Why? jwt?

제가 알기로는 session 방식에서는 처음 로그인 시에는 세션에 사용자 정보를 저장하고, 
이후 요청 시 세션을 확인하여 사용자를 확인하는 방식인 것으로 알고 있습니다.

특정한 사용자가 로그인 이후 요청을 할 때, SeucirtyContextHolder에 사용자 정보가 저장되어 있기 때문에 
이를 비교하는 방식으로 진행되며, 이때는 인증 정보를 서버에서 관리하고 세션 쿠키를 이용해서 클라이언트와 상호작용하게 됩니다.

하지만 세션 인증에 리소스 소비가 심하다는 단점을 가지고 있는 것으로 알고 있습니다.
또한 서버 확장성을 떨어뜨리게 되는데, 저희 서비스 특성 상 

1. 요양보호사의 차트 입력 등은 비정기적으로 특정 시간에 몰려서 발생할 확률이 높고
2. 알림서비스 등도 이와 같이 대부분의 사용자가 Default 설정을 사용할 확률이 높아 특정 시간에 몰릴 것이라 예상됩니다.

즉, 특정 시간에만 트래픽이 증가할 것으로 추측되며, 이때 Scale-out 전략을 취할 확률이 높고,
stateless한 서버를 유지하는 것이 중요하다고 판단되어 세션 방식은 적합하지 않다고 판단하였습니다.
따라서 JWT 방식을 선택하게 되었습니다.