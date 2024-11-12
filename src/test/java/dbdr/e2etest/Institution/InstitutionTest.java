package dbdr.e2etest.Institution;


import static org.assertj.core.api.Assertions.assertThat;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.global.util.api.ApiUtils.ApiResult;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import dbdr.testhelper.TestHelperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstitutionTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestHelperFactory testHelperFactory;

    TestHelper testHelper;


    @Test
    @DisplayName("신규 요양원 등록")
    public void addInstitutionTest() {

        //given
        Admin admin = Admin.builder().loginId("testadmin").loginPassword("adminpassword").build();
        testHelperFactory.addAdmin(admin);
        testHelper = testHelperFactory.create(port);

        InstitutionRequest institutionRequest = new InstitutionRequest(123123L, "김치덮밥요양원", "institutuion1", "password");
        //when

        //서버 관리자가 신규 요양원을 등록한다.
        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution").requestBody(institutionRequest).post()
                .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {}).getBody();


        //then

        assertThat(response.success()).isTrue();
        assertThat(response.response().institutionName()).isEqualTo("김치덮밥요양원");
        assertThat(response.response().institutionNumber()).isEqualTo(123123L);
    }
/*
    @Test
    @DisplayName("요양원 번호 중복 등록 방지")
    public void addInstitutionTest2() {
        //given

        Institution institution = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L)
            .loginId("institutuion1")
            .loginPassword("password")
            .build();

        InstitutionRequest institutionRequest = emm.getMapper(Institution.class).toRequest(institution);

        //요양원 번호 중복됨
       Institution institution2 = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L)
            .loginId("institutuion2")
            .loginPassword("password")
            .build();

        InstitutionRequest institutionRequest2 = emm.getMapper(Institution.class).toRequest(institution2);

        //요양원 이름 중복됨
        Institution institution3 = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123125L)
            .loginId("institutuion1")
            .loginPassword("password")
            .build();

        InstitutionRequest institutionRequest3 = emm.getMapper(Institution.class).toRequest(institution3);

        //when

        //요양원 등록
        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution").requestBody(institutionRequest).post()
            .toEntity(InstitutionResponse.class);

        //then

        //중복없는 요양원 등록 성공
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //요양원 번호가 중복되는 경우
        assertThatThrownBy(() -> {
            testHelper.user(Role.ADMIN, "testadmin", "adminpassword").uri("/admin/institution")
                .requestBody(institutionRequest2).post().toEntity(InstitutionResponse.class);
        }).hasMessageContaining(ApplicationError.DUPLICATE_INSTITUTION_NUMBER.getMessage());

        //요양원 이름이 중복되는 경우
        assertThatThrownBy(() -> {
            testHelper.user(Role.ADMIN, "testadmin", "adminpassword").uri("/admin/institution")
                .requestBody(institutionRequest3).post().toEntity(InstitutionResponse.class);
        }).hasMessageContaining(ApplicationError.DUPILCATE_INSTITUTION_NAME.getMessage());

    }

    @Test
    @DisplayName("요양원 정보 수정")
    public void updateInstitutionTest() {
        //given

        Institution institution = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L)
            .loginId("institutuion1")
            .loginPassword("password")
            .build();

        InstitutionRequest institutionRequest = emm.getMapper(Institution.class).toRequest(institution);

            //요양원 등록
        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution")
            .requestBody(institutionRequest)
            .post()
            .toEntity(InstitutionResponse.class);

            //요양원 생성 response에서 id를 추출
        long id = response.getBody().id();

            //수정할 요양원 정보 : 요양원 이름과 번호 변경
        InstitutionRequest updateRequest =
            new InstitutionRequest(1233L, "김치찌개요양원", "institutuion3232", "password");

        //when

            //요양원 정보 수정
         var updateResponse = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
             .uri("/admin/institution/"+id) //pathVariable로 id를 넘겨줌.
             .requestBody(updateRequest)
             .put()
             .toEntity(InstitutionResponse.class);

        //then

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().institutionName()).isEqualTo("김치찌개요양원");
        assertThat(updateResponse.getBody().institutionNumber()).isEqualTo(1233L);
    }

    @Test
    @DisplayName("본인 요양원만 접근 가능, 타 요양원 접근 거부")
    void institutionAuthTest(){
        //given

        //요양원 1 생성
        Institution institution = Institution.builder().institutionName("김치덮밥요양원")
            .institutionNumber(123123L)
            .loginId("institutuion1")
            .loginPassword("password")
            .build();

        InstitutionRequest institutionRequest = emm.getMapper(Institution.class).toRequest(institution);

         //요양원 2 생성
        Institution institution2 = Institution.builder().institutionName("카레요양원")
            .institutionNumber(1231443L)
            .loginId("institutuion2")
            .loginPassword("password")
            .build();

        InstitutionRequest institutionRequest2 = emm.getMapper(Institution.class).toRequest(institution2);
        //서버 관리자가 요양원들을 등록함

        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution")
            .requestBody(institutionRequest)
            .post()
            .toEntity(InstitutionResponse.class);

        var response2 = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution")
            .requestBody(institutionRequest2)
            .post()
            .toEntity(InstitutionResponse.class);

        //요양원 id
        long id = response.getBody().id();
        long id2 = response2.getBody().id();

        //when

        //보인 요양원 정보 조회
        var showResponse = testHelper.user(Role.INSTITUTION, "institutuion1", "password")
            .uri("/admin/institution/"+id)
            .get()
            .toEntity(InstitutionResponse.class);

        assertThat(showResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //본인 요양원 정보 수정 시도
        var updateResponse = testHelper.user(Role.INSTITUTION, "institutuion1", "password")
            .uri("/admin/institution/"+id)
            .requestBody(institutionRequest)
            .put()
            .toEntity(InstitutionResponse.class);

        //then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().institutionName()).isEqualTo("김치덮밥요양원");
        assertThat(updateResponse.getBody().institutionNumber()).isEqualTo(123123L);

        //타 요양원 정보 수정 시도 실패
        assertThatThrownBy(() -> {
            testHelper.user(Role.INSTITUTION, "institutuion1", "password")
                .uri("/admin/institution/"+id2) //다른 요양원에 접근
                .requestBody(institutionRequest2)
                .put()
                .toEntity(InstitutionResponse.class);
        }).hasMessageContaining(ApplicationError.ACCESS_NOT_ALLOWED.name());

    }

    @Test
    @DisplayName("요양보호사는 요양원 Role에 접근 불가 테스트")
    void testGuardianInsti(){
        Institution institution = Institution.builder()
            .institutionName("김치덮밥요양원")
            .institutionNumber(123123L)
            .loginId("institutuion1")
            .loginPassword("password")
            .build();

        Careworker careworker = Careworker.builder()
                .loginId("careworker1")
                .loginPassword("password")
                .name("김치밥")
                .phone("01012345678")
                .institution(institution)
                .email("abcd@gmail.com")
                .build();

        Recipient recipient = Recipient.builder()
                .name("Dummy Name")
                .birth(LocalDate.of(1980, 1, 1))
                .gender("Male")
                .careLevel("Level 1")
                .careNumber("123456789")
                .startDate(LocalDate.now())
                .institution(institution)
                .institutionNumber(1L)
                .careworker(careworker)
                .build();

        Guardian guardian = Guardian.builder()
            .loginPassword("password")
            .name("김보호자")
            .loginId("01012341234")
            .phone("01012341234")
            .recipient(recipient)
            .build();


        //요양원 생성
        InstitutionRequest institutionRequest = emm.getMapper(Institution.class).toRequest(institution);


        var response = testHelper.user(Role.ADMIN, "testadmin", "adminpassword")
            .uri("/admin/institution")
            .requestBody(institutionRequest)
            .post()
            .toEntity(InstitutionResponse.class);

        long institutionId = response.getBody().id();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //요양원에서 요양보호사 등록
        CareworkerRequestDTO careworkerRequestDTO =
            new CareworkerRequestDTO("password", institutionId,"김치국", "abcd@gmail.com","01012311231");

        var response2 = testHelper.user(Role.INSTITUTION, "institutuion1", "password")
            .uri("/careworker/"+institutionId)
            .requestBody(careworkerRequestDTO)
            .post()
            .toEntity(InstitutionResponse.class);

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //요양보호사가 요양원 Role에 접근 시도
        assertThatThrownBy(() -> {
            testHelper.user(Role.CAREWORKER, "01012311231", "password")
                .uri("/admin/institution/"+institutionId)
                .get()
                .toEntity(InstitutionResponse.class);
        }).hasMessageContaining(ApplicationError.ACCESS_NOT_ALLOWED.name());


    }



 */

}

