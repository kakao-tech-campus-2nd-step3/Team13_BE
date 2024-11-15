# 테스트 코드 작성 요령

## 1. Test Helper 사용법

### 1. 'TestHelper' 클래스는 RestClient를 이용하여 통합테스트를 돕기 위해 작성됨.

### 2. Test 클래스 내부 필요 어노테이션

- @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

- Admin Service : 관리자 생성 기능은 내부의 서비스 클래스를 호출하여 사용
    @Autowired
    AdminService adminService;

- 모든 Spring Bean을 주입받아 사용할 수 있도록 설정
    @LocalServerPort
    private int port;

### 3. TestHelper 기본 사용법

```java
    import dbdr.testhelper.TestHelper;

@Test
public void test() {
    // init : 이 떄 각 테스트마다 서버 관리자의 ID가 중복 불가능
    String loginId = "admin";
    String password = "admin";
    Admin admin = new Admin(loginId, password);
    adminService.addAdmin(admin);

    // given

    //Request 객체를 생성합니다.

    // when
    TestHelper testHelper = new TestHelper(port);
    var response = testHelper.user(Role,loginId,password).uri("").requestBody(request).post();
}
```

user() 메소드 내부에 접근하고 하는 사용자의 역할과 로그인 아이디 비밀번호를 입력하면, testHelper에서 login이후
JWT를 가져와 자동으로 header에 넣어줍니다.

reqeustBody()는 특정 requestbody가 필요 시 적용할 수 있습니다.

uri()는 기본 도메인주소와 현재 api 버전까지가 포함되어있으며, 실질적으로 테스트하고자하는 주소를 입력하면 됩니다.

### 4. 반환 타입의 처리

컨트롤러에서 반환 타입은 ApiUtils를 통해서 처리됩니다.

- 성공 응답에 대한 assertThat

import dbdr.global.util.api.ApiUtils.ApiResult;
import org.springframework.core.ParameterizedTypeReference;

var body = response.toEntity(new ParameterizedTypeReference<ApiResult<"원하는 Response">>(){}).getBody();

예를 들어, 만약 GuardianResponse를 반환하는 주소로 요청을 보낸다면, "원하는 Response"에 GuardianResponse를 넣어주면 됩니다.


- 실패 응답에 대한 assertThatThrownBy

assertThatThrownBy(() -> testHelper.user().uri("").requestBody(request).post())
    .toEntity(new ParameterizedTypeReference<ApiResult<"원하는 Response">>(){})
    .hasMessageContaining("원하는 에러 메시지");

이를 통해 응답에 대한 테스트를 진행할 수 있습니다.

