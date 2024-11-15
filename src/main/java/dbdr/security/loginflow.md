# 로그인

# 1. 회원의 로그인 시도

회원은 로그인과 관련된 정보를 request body에 실어서 컨트롤러로 보냅니다.

```java
//request 객체
public record LoginRequest(@NotBlank(message = "유저 ID는 필수 항목입니다.") String userId,
                           @NotBlank(message = "비밀번호는 필수 항목입니다.") String password) {

}
```

<컨트롤러>

```java

//LoginController.java 내부

@PostMapping("/{role}") //1번줄
    public ResponseEntity<Void> login(@PathVariable("role") String role,
        @RequestBody @Valid LoginRequest loginRequest) {
        Role roleEnum = roleCheck(role);
        String token = loginService.login(roleEnum, loginRequest);
        return ResponseEntity.ok().header(authHeader, token).build();
    }
```

### 컨트롤러 내부 1번줄

이떄, URL을 기반으로 Role을 구분할 수 있도록 하였습니다.

예를 들어, Guardian의 경우 URL은 “http:// 우리의주소/v1/login/guardian”

으로 요청을 보내게 됩니다.

해당 요청은 아무 사용자나 보낼 수 있습니다. - 필터를 그냥 쑥 통과해서요. 왜냐?!

- 필터가 뭔데요
    
    
    스프링 시큐리티의 동작방식을 가볍게 설명해보면, 스프링 시큐리티는 서블릿 필터에서 동작하게 됩니다.
    
    서블릿 필터는 스프링의 디스패쳐 서블릿으로 가기 전 request를 가로채 필요한 작업을 진행하게되고, 우리는 스프링 시큐리티를 이용해서 해당 서블릿 필터에 필요한 작업을 추가하게 됩니다.
    
    ![스크린샷 2024-10-11 오후 1.35.29.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8dc8dc5a-e7b1-4d8f-b454-1657415061ea/e79a38a8-da08-4879-8404-651962e6c0ec/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-10-11_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_1.35.29.png)
    
    이때 스프링빈으로 등록한 시큐리티를 위한 서비스들은 필터단계에서는 동작을 할 수 없는데요 (아직 오지 않았음으로)
    
    스프링 시큐리티는 DelegatingFilterProxy를 이용해 해당 빈을 가져와 의존성을 주입받습니다.
    

```java
//SecurityConfig.java

http.authorizeHttpRequests((authorize) -> {
                authorize
                    .requestMatchers("/*/login/*").permitAll()
                    .anyRequest().authenticated();
            });
```

이 부분은 스프링 시큐리티 설정파일이 위치한 부분입니다.

여기 requestMatchers( ) 에서 authorize가 없어도 permitAll()을 해주고 있기에 해당 url은 접근 가능해집니다.

# 2. login service로 가자!

request는 로그인 서비스로 오게 됩니다.

```java
 ///LoginService.java
 
 @Transactional
    public String login(Role role, LoginRequest loginRequest) {
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .userLoginId(loginRequest.userId())
            .password(loginRequest.password())
            .role(role.name())
            .build(); //1번줄

        //2번줄
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, loginRequest.password());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtProvider.createAccessToken(authentication.getName(), role.name(), jwtExpiration);
    }
```

### 1번줄

처음에는 BaseUserDetails 를 생성하고 있습니다.

BaseUserDetails는 UserDetails 라고하는 인터페이스를 구현한 객체입니다.

UserDetails는 스프링 시큐리티에서 기본적으로 제공하는 “회원”에 대한 객체이며,

저희도 이를 기반으로 동작하고자 구현하였습니다.

### 2번줄

```java
UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, loginRequest.password());
Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
```

이 부분에서는 AuthenticationToken을 만들어서 회원에 대해 아직 인증되지않은 객체를 SecurityContextHolder 로 보내는 작업을 하고 있습니다.

- 왜 UsernamePasswordAuthenticatonToken을 만들어야해요?
    
    AuthenticationToken ( 구현체 → UsernamePasswordAuthenticationToken) 은 SeucrityContextHolder 에서 사용하는 인증을 위한 객체로 사용됩니다.
    
    하나의 요청이 있으면 해당 요청에 대한 작업이 끝날때까지 인증에 대한 정보를 보관해야합니다. (저희는 세션을 사용하지 않는 stateless 방식이므로 - 사용자에 대한 정보를 서버에서 보관하지 않는다는 뜻 - DB서버이야기 아닙니다!)
    
    이 때 SeucrityContextHolder가 해당 정보를 보관하고 있으며 우리는 컨트롤러 혹은 서비스 어디에서든지 해당 Context에서 요청자에 대한 정보를 얻을 수 있게 됩니다.
    
    ```java
    //스프링 빈 어디에서든지 인증정보를 꺼내볼 수 있어요
    
    SecurityContextHolder.getContext().getAuthentication();
    // Autentication 객체가 반환됩니다.
    ```
    
    근데 서비스의 구현에 따라서 인증된 사용자를 구분하는데 방식이 바뀔 수 있으므로 스프링시큐리티는 AuthenticationToken이라는 추상클래스를 통해서 자율성을 부여해주고 있습니다.
    

### 3. AuthenticationManager

대부분의 애매함은 여기서 나올 것이라 추측됩니다.

```java
Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

return jwtProvider.createAccessToken(authentication.getName(), role.name(), jwtExpiration);
```

진솔님이 말씀하신 로그인 실패 로직이라던지… 그런 부분이 여기에는 전혀 없는 모습으로 보이는데요

저도 한 3일정도를 스프링시큐리티해석을 위해 공부하고도 ‘어 왜 없지? 내가 실수했나보군’ 하면서 다시 공부해서 알아낼정도로 은밀하게 되어있더군요

위에 설명에서 “인증되지 않은 객체”라고 표현하였습니다.

이는 Authentication 객체는 `authentication.isAuthenticated()` 라는 메소드를 가지고 있고, 인증과정이 따로 있다는 뜻이지요.

# 3. 인증과정

### 1. 너말고 매니저 불러와

`authenticationManagerBuilder.getObject()` 에서 인증 매니저를 가져오고
`.authenticate(authenticationToken)` 를 통해 인증되지않은 객체를 인증시켜달라고 위임합니다.

### 2. 매니저 : 인증 담당자 나와라그래

스프링시큐리티에 대해 이거 관련된 설정 어디 있지? 생각된다면 → SecurityConfig 를 참고하시면 되는데요

`.authenticationProvider(baseAuthenticationProvider())`

여기를 보니면 인증담당자(공급자)로 커스텀한 Provider를 구현한 모습을 볼 수 있습니다.

즉, AuthenticationManager는 SecurityConfig를 통해서AuthenticationProvider를 포함하고 있고, 인증되지않은 객체에 대해 인증과정을 해당 provider에게 위임하게 되는 겁니다. (매니저는 관리직이래요)

- 그럼 꼭 Provider를 구현해야하나요?
    
    기본적으로 따로 authenticationProvider를 등록하지않는다면 UsernamePasswordToken에 대해서 스프링 시큐리티가 기본으로 제공하는 DaoAuthenticationProvider(이하 Dao) 가 그 역할을 하게 됩니다.
    
    Dao는 UserDetailsService이 구현된 Bean을 찾아서 @Override된 loadUserbyname() 메소드를 호출해서 인증을 제공합니다.
    
    저희는 Role에 따라 username이 같아질 수 있는데, 기본 DaoAuthenticationProvider 내부 코드를 보시면
    
    `UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);`
    
    해당 방식으로 UserDetailsService에서 username을 검증하게되고, 이는 Careworker와 Guardian이 같은 번호를 쓰는 경우 충돌하게되는 문제가 생겨 직접 구현하게 되었습니다.
    

### 3. 제가 인증담당자입니다.

```java
public class BaseAuthenticationProvider implements AuthenticationProvider {

    private final BaseUserDetailsService baseUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        BaseUserDetails unAuthUser = (BaseUserDetails) authentication.getPrincipal();
        BaseUserDetails authUser = baseUserDetailsService.loadUserByUsernameAndRole(unAuthUser.getUserLoginId(), unAuthUser.getRole());

        if (!passwordEncoder.matches(unAuthUser.getPassword(), authUser.getPassword())) {
            throw new ApplicationException(ApplicationError.PASSWORD_NOT_MATCH);
        }
        return new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("supports : {}", authentication.equals(UsernamePasswordAuthenticationToken.class));
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

여기를 보면 authenticate 가 Override 된 모습을 볼 수 있습니다.

즉, 위의 서비스에서 getObject().authenticate( ) 에서 해당 코드가 호출되는 것이지요.

### 한줄씩 보기

`BaseUserDetails unAuthUser = (BaseUserDetails)authentication.getPrincipal();`

Authentication 객체에서 Principal()을 얻어내고 있는데요, principal는 user에 대한 구분할 수 있는 정보를 담고 있는 객체입니다. 

Object 타입으로 반환되고, 이를 casting 해주었습니다.

- Princi…pal? 시…팔? 이 어디서 들어갔는데요?
    
    `UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, loginRequest.password());`
    
    여기 LoginService에서 생성자에 userDetails 이 principal로 등록되었습니다!
    

`BaseUserDetails authUser = baseUserDetailsService.loadUserByUsernameAndRole(unAuthUser.getUserLoginId(), unAuthUser.getRole());`

여기서는 위에서 얻어낸 인증되지않은 user의 정보를 이용해 baseUserDetailsService를 통해서 DB에서 회원의 정보가 있는 지 조회하게 됩니다.

```java
 //BaseUserDetailsService.java
 public BaseUserDetails loadUserByUsernameAndRole(String username, Role role) {

        if (role == Role.GUARDIAN) {
            return getGuadianDetails(username);
        }
        if (role == Role.CAREWORKER) {
            return getCareWorkerDetails(username);
        }
        if (role == Role.INSTITUTION) {
            return getInstitutionDetails(username);
        }
        if (role == Role.ADMIN) {
            return getAdminDetails(username);
        }

        throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
    }
```

참고 : 메서드명은 기존은 Dao와 비슷하게 짓기 위해서 저렇게 하였습니다.

그리고 비밀번호 일치를 확인한 뒤 (이때 DB에 저장된 비밀번호는 암호화된 상태로 저장되어있고, passwordEncoder를 사용해서 암호화된 값이 같은 지 검증하게 됩니다.)

`return new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());`

인증된 토큰을 만들어서 반환하게 됩니다.

- 서비스에서 만들어준 UsernamePasswrdAuthenticationToken과 같은 것 같은데 인증된 객체와 인증되지않은 객체는 어떻게 비교하나요?
    
    ```java
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
            super((Collection)null);
            this.principal = principal;
            this.credentials = credentials;
            this.setAuthenticated(false);
        }
    
        public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
            super(authorities);
            this.principal = principal;
            this.credentials = credentials;
            super.setAuthenticated(true);
        }
        
        //생성자에서 authorites를 넣고 안넣고에 따라서 구분됩니다!
    ```
    

- Override 된 supports 메소드는 뭐임요?
    
    인증 토큰의 종류에 따라서 Provider가 해당 토큰을 지원하는 지 유무를 체크할 수 있게 해줍니다. 이를 통해 OAuth와 같은 다양한 인증 토큰을 통한 로그인을 지원할 수 있지 않을까요…? (추측)
    

### JWT 반환하기

로그인이 실패한다면? → authenticate() 메소드 내부에서 처리하게 됩니다. → 이는 SecurityConfig에서

```java
.exceptionHandling((exception) -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 거부");
            })
            .authenticationEntryPoint((request, response, authException) -> {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
            }));
```

이렇게 핸들링하게 됩니다. (추후 AOP처리된 global exception handle로 넘길까 합니다!)

로그인이 성공한다면? → jwtProvider를 통해서 토큰을 만들어 반환하게 됩니다.

(아마 refresh 토큰을 도입 시, 해당 부분의 return을 access token과 refresh token을 합친 DTO를 만들어서 반환하게 될 것 같습니다.)

### 끝.