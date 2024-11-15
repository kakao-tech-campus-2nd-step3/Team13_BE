# 인증과정

# 1. 필터에 걸렸다…!

```java
//SecurityConfig.java

http.addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class) //jwt 필터 추가
    .authenticationProvider(baseAuthenticationProvider()) //인증 프로바이더 추가
    .authorizeHttpRequests((authorize) -> {
                authorize
                    .requestMatchers("/*/login/*").permitAll()
                    .anyRequest().authenticated();
            });
```

- 님 코드에는 SecurityConfig는 http.csrf().cors(). …. 인데 왜 여기는 http.addFilterBefore 임요?
    
    각 메소드에서 계속해서 HttpSecurity 객체를 반환하고 있습니다!
    

여기서 anyRequest() 에 대해서는 authenticated(); 를 요구하고 있습니다.

즉, 로그인을 제외한 요청에 대해서는 인증된 사용자만 가능한 것입니다!

(추후 admin은 localhost에서만 접속해서 추가할 수 있게 하기 or 따로 루트를 열어둬야할 것 같습니다)

그럼 인증은 어디서 진행되는가? → addFilterBefore의 JwtFilter에서 인증을 진행하게 됩니다.

이는 UsernamePasswordAuthenticationFilter 전에 추가되어 request에서 전달된 정보를 통해서 인증에 성공할 경우 SecurityContext 에 인증된 객체를 추가하게 됩니다.

# 2. 필터 내부의 처리

```java
//JwtFilter.java

String token = request.getHeader("Authorization");

if (token != null) {
            try {
                jwtProvider.validateToken(token);
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.debug("토큰 유저 정보 추출 실패 : {}", e.getMessage());
            }
        }
```

필터 내부는 다음과 같습니다.

request의 header에서 key가 “Authorization”인 필드를 추출합니다.

(클라이언트가 헤더에 해당 이름으로 JWT를 넣어주어야합니닷!)

이후 jwtProvider에게 토큰을 검사하도록 합니다. (accessToken이 맞는지, 만료된 것은 아닌지 등등…)

성공한다면? → SecurityContext에 Authentication 객체를 등록하게 됩니다.

```java
//JwtProvider.java
public Authentication getAuthentication(String token) {
        BaseUserDetails userDetails = baseUserDetailsService.loadUserByUsernameAndRole(
            getUserName(token), Role.valueOf(getRole(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
            userDetails.getAuthorities());
    }
```

이전 로그인 편에서 보시듯, Authentication객체의 구현체인 UsernamePasswordAuthenticationToken에 Authorities를 포함한 생성자를 이용해 인증된 객체로 반환하게 됩니다.

---

### 스프링 시큐리티에서 Context 관리 전략

저희는 stateless 서버를 지향합니다.

스프링에서 하나의 요청에 대해서 서버의 pool에서 스레드를 가져오게 되고 해당 스레드의 라이프 사이클은 해당 요청에 대한 응답이 끝날때 까지 입니다.

스프링 시큐리티는 스레드마다 Context를 유지하고, 요청이 끝나면 해당 Context 를 clear하게 됩니다.(로그찍으면 볼 수 있어요!) → 따라서 stateless

저희 서비스에서는 SecurityContext는 요청이 들어오면 JWTFilter에서 jwt를 통해 인증이 되면 SecurityContext에 해당 정보를 저장하게 되고, 해당 Context는 요청 → 응답 까지만 유효하게 됩니다.