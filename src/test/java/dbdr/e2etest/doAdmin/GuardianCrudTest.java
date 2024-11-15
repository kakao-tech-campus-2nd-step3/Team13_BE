package dbdr.e2etest.doAdmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.service.AdminService;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.request.GuardianUpdateRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.global.util.api.ApiUtils.ApiError;
import dbdr.global.util.api.ApiUtils.ApiResult;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GuardianCrudTest {


    @Autowired
    AdminService adminService;

    @LocalServerPort
    private int port;

    private final long testAlpha_number = 3;

    /*
    사용 번호 c1~
     */

    private void addAdmin(String adminId, String adminpassword) {
        Admin admin = Admin.builder()
            .loginId(adminId)
            .loginPassword(adminpassword)
            .build();
        adminService.addAdmin(admin);
    }

    private Long addInstitutionRequestLogic(String adminId, String adminpassword,
        String testAlpha, String testNumber) {
        Long institutionNumber = 1000L * testAlpha_number + Long.parseLong(testNumber) * 100;
        String institutionName = "institution_" + testAlpha + testNumber;
        String institutionLoginId = "institution_" + testAlpha + testNumber;
        String institutionLoginPassword = "institution_" + testAlpha + testNumber;

        InstitutionRequest institutionRequest = new InstitutionRequest(institutionNumber,
            institutionName, institutionLoginId, institutionLoginPassword);

        TestHelper testHelper = new TestHelper(port);
        return testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/institution")
            .requestBody(institutionRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<InstitutionResponse>>() {
            })
            .getBody().response().id();
    }

    @Test
    @DisplayName("서버 관리자가 요양원에 보호자 추가")
    void test_c1() {
        String adminId = "admin_c1";
        String adminpassword = "admin_c1";
        addAdmin(adminId, adminpassword);

        String testNumber = "1";
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, "c", testNumber);

        TestHelper testHelper = new TestHelper(port);

        //given
        String guardianName = "guardian_c1";
        String guardianPhone = "01033333334";
        String guardianLoginPassword = "guardian_c1";

        GuardianRequest guardianRequest = new GuardianRequest(guardianPhone, guardianName,
            guardianLoginPassword,institutionId);

        //when
        var response = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        //then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().name()).isEqualTo(guardianName);
        assertThat(response.response().phone()).isEqualTo(guardianPhone);

    }

    @Test
    @DisplayName("서버관리자가 요양원이 없는 경우 보호자 추가 불가")
    void test_c2() {
        String adminId = "admin_c2";
        String adminpassword = "admin_c2";
        addAdmin(adminId, adminpassword);

        TestHelper testHelper = new TestHelper(port);

        //given
        String guardianName = "guardian_c2";
        String guardianPhone = "01033333335";
        String guardianLoginPassword = "guardian_c2";

        GuardianRequest guardianRequest = new GuardianRequest(guardianPhone, guardianName,
            guardianLoginPassword, 0L);

        //when
        assertThatThrownBy(() -> testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })).hasMessageContaining("404");
    }

    @Test
    @DisplayName("서버 관리자가 요양원에 보호자 추가 후 수정")
    void test_c3(){
        String adminId = "admin_c3";
        String adminpassword = "admin_c3";
        addAdmin(adminId, adminpassword);

        String testNumber = "3";
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, "c", testNumber);

        TestHelper testHelper = new TestHelper(port);

        //given
        String guardianName = "guardian_c3";
        String guardianPhone = "01033333336";
        String guardianLoginPassword = "guardian_c3";

        GuardianRequest guardianRequest = new GuardianRequest(guardianPhone, guardianName,
            guardianLoginPassword,institutionId);

        //when
        var response = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        //then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().name()).isEqualTo(guardianName);
        assertThat(response.response().phone()).isEqualTo(guardianPhone);

        // given
        //
        //
        // 기존 보호자 정보 수정하기

        long id = response.response().id();
        String updateGuardianName = "update_guardian_c3";
        String updateGuardianPhone = "01033333337";
        String updateGuardianLoginPassword = "update_guardian_c3";

        GuardianUpdateRequest guardianUpdateRequest = new GuardianUpdateRequest(updateGuardianPhone, updateGuardianName);

        //when
        var updateResponse = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian/" + id)
            .requestBody(guardianUpdateRequest)
            .put()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        //then
        assertThat(updateResponse).isNotNull();
        assertThat(updateResponse.success()).isTrue();
        assertThat(updateResponse.response().name()).isEqualTo(updateGuardianName);
        assertThat(updateResponse.response().phone()).isEqualTo(updateGuardianPhone);
    }

    @Test
    @DisplayName("서버 관리자가 요양원의 보호자 삭제")
    void test_c4(){
        String adminId = "admin_c4";
        String adminpassword = "admin_c4";
        addAdmin(adminId, adminpassword);

        String testNumber = "4";
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, "c", testNumber);

        TestHelper testHelper = new TestHelper(port);

        //given
        String guardianName = "guardian_c4";
        String guardianPhone = "01033333338";
        String guardianLoginPassword = "guardian_c4";

        GuardianRequest guardianRequest = new GuardianRequest(guardianPhone, guardianName,
            guardianLoginPassword,institutionId);

        //when
        var response = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        //then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.response().name()).isEqualTo(guardianName);
        assertThat(response.response().phone()).isEqualTo(guardianPhone);

        // given
        //
        //
        // 기존 보호자 정보 삭제하기

        long id = response.response().id();

        //when
        var deleteResponse = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian/" + id)
            .delete()
            .toEntity(new ParameterizedTypeReference<ApiResult<String>>() {
            });

        //then
        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("서버관리자가 다수의 요양원에 있는 모든 보호자 조회")
    void test_c5678(){
        String adminId = "admin_c5";
        String adminpassword = "admin_c5";
        addAdmin(adminId, adminpassword);

        String testNumber = "5";
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, "c", testNumber);
        Long institutionId2 = addInstitutionRequestLogic(adminId, adminpassword, "c", "6");
        Long institutionId3 = addInstitutionRequestLogic(adminId, adminpassword, "c", "7");
        Long institutionId4 = addInstitutionRequestLogic(adminId, adminpassword, "c", "8");

        TestHelper testHelper = new TestHelper(port);

        //given
        String guardianName = "guardian_c5";
        String guardianPhone = "01033333339";
        String guardianLoginPassword = "guardian_c5";

        GuardianRequest guardianRequest = new GuardianRequest(guardianPhone, guardianName,
            guardianLoginPassword,institutionId);

        String guardianName2 = "guardian_c6";
        String guardianPhone2 = "01033333340";
        String guardianLoginPassword2 = "guardian_c6";

        GuardianRequest guardianRequest2 = new GuardianRequest(guardianPhone2, guardianName2,
            guardianLoginPassword2,institutionId2);

        String guardianName3 = "guardian_c7";
        String guardianPhone3 = "01033333341";
        String guardianLoginPassword3 = "guardian_c7";

        GuardianRequest guardianRequest3 = new GuardianRequest(guardianPhone3, guardianName3,
            guardianLoginPassword3,institutionId3);

        String guardianName4 = "guardian_c8";
        String guardianPhone4 = "01033333342";
        String guardianLoginPassword4 = "guard";

        GuardianRequest guardianRequest4 = new GuardianRequest(guardianPhone4, guardianName4,
            guardianLoginPassword4,institutionId4);



        //when
        testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest2)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest3)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();

        testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest4)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody();



        // given
        //
        //
        // 모든 보호자 조회하기

        //when
        var allGuardianResponse = testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .get()
            .toEntity(new ParameterizedTypeReference<ApiResult<List<GuardianResponse>>>() {
            })
            .getBody();

        //then
        assertThat(allGuardianResponse).isNotNull();
        assertThat(allGuardianResponse.success()).isTrue();
        assertThat(allGuardianResponse.response().size()).isGreaterThan(0);
        assertThat(allGuardianResponse.response().stream().anyMatch(guardianResponse -> guardianResponse.name().equals(guardianName))).isTrue();
        assertThat(allGuardianResponse.response().stream().anyMatch(guardianResponse -> guardianResponse.name().equals(guardianName2))).isTrue();
        assertThat(allGuardianResponse.response().stream().anyMatch(guardianResponse -> guardianResponse.name().equals(guardianName3))).isTrue();
        assertThat(allGuardianResponse.response().stream().anyMatch(guardianResponse -> guardianResponse.name().equals(guardianName4))).isTrue();

    }


}
