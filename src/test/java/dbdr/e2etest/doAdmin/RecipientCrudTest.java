package dbdr.e2etest.doAdmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.service.AdminService;
import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.dto.response.GuardianResponse;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.dto.response.InstitutionResponse;
import dbdr.domain.recipient.dto.request.RecipientRequest;
import dbdr.domain.recipient.dto.response.RecipientResponse;
import dbdr.global.util.api.ApiUtils.ApiResult;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecipientCrudTest {

    @Autowired
    AdminService adminService;

    @LocalServerPort
    private int port;

    private final long testAlpha_number = 4L;

    /*
    사용Number : d1~
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

    private Long addGuardianRequestLogic(String adminId, String adminpassword,
        String testAlpha, String testNumber, Long institutionId, String phoneNumber) {

        String guardianName = "guardian_" + testAlpha + testNumber;
        String guardianLoginPassword = "guardian_" + testAlpha + testNumber;

        GuardianRequest guardianRequest = new GuardianRequest(phoneNumber,
            guardianName, guardianLoginPassword, institutionId);

        TestHelper testHelper = new TestHelper(port);
        return testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/guardian")
            .requestBody(guardianRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<GuardianResponse>>() {
            })
            .getBody().response().id();
    }

    private Long addCareworkerRequestLogic(String adminId, String adminpassword,
        String testAlpha, String testNumber, Long institutionId, String phoneNumber) {

        String careworkerName = "careworker_" + testAlpha + testNumber;
        String careworkerLoginPassword = "careworker_" + testAlpha + testNumber;
        String careworkerEmail = "careworker_" + testAlpha + testNumber + "@naver.com";

        CareworkerRequest careworkerRequest = new CareworkerRequest(institutionId, careworkerName,
            careworkerEmail, phoneNumber, careworkerLoginPassword);

        TestHelper testHelper = new TestHelper(port);
        return testHelper.user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/careworker")
            .requestBody(careworkerRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<CareworkerResponse>>() {
            })
            .getBody().response().getId();
    }

    @Test
    @DisplayName("서버관리자가 돌봄대상자 추가")
    void test_d1() {
        //given
        String adminId = "admin1";
        String adminpassword = "admin1";
        addAdmin(adminId, adminpassword);

        //요양원 추가
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, "d", "1");

        //보호자 추가
        String phoneNumber = "01044123212";
        Long guardianId = addGuardianRequestLogic(adminId, adminpassword, "d", "1", institutionId,
            phoneNumber);

        //요양보호사 추가
        phoneNumber = "01044123213";
        Long careworkerId = addCareworkerRequestLogic(adminId, adminpassword, "d", "1",
            institutionId, phoneNumber);

        //돌봄대상자 정보
        String recipientName = "recipient_d1";
        LocalDate birth = LocalDate.of(1981, 8, 1);
        String gender = "남";
        String careLevel = "2등급";
        String careNumber = "L0000000230-300";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        String institution = "institution_d1";
        Long institutionNumber = 777999L;

        RecipientRequest recipientRequest = new RecipientRequest(recipientName, birth, gender,
            careLevel, careNumber, startDate
            , institution, institutionNumber, institutionId, careworkerId, guardianId);

        //when

        var response = new TestHelper(port)
            .user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/recipient")
            .requestBody(recipientRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<RecipientResponse>>() {
            }).getBody();

        //then
        assertThat(response.success()).isTrue();
        assertThat(response.response().getName()).isEqualTo(recipientName);
        assertThat(response.response().getBirth()).isEqualTo(birth);
        assertThat(response.response().getGender()).isEqualTo(gender);
    }

    @Test
    @DisplayName("보호자가 존재하지않는 경우 실패")
    void test_d2() {
        //given
        String adminId = "admin_d1";
        String adminpassword = "admin_d1";
        addAdmin(adminId, adminpassword);

        //요양원 추가
        Long institutionId = addInstitutionRequestLogic(adminId, adminpassword, "d", "2");

        //요양보호사 추가
        String phoneNumber = "01043213213";
        Long careworkerId = addCareworkerRequestLogic(adminId, adminpassword, "d", "2",
            institutionId, phoneNumber);

        //돌봄대상자 정보
        String recipientName = "recipient_d2";
        LocalDate birth = LocalDate.of(1981, 8, 1);
        String gender = "남";
        String careLevel = "2등급";
        String careNumber = "L0000032230-300";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        String institution = "institution_d1";
        Long institutionNumber = 777999L;

        Long guardianId = 100000L;

        RecipientRequest recipientRequest = new RecipientRequest(recipientName, birth, gender,
            careLevel, careNumber, startDate
            , institution, institutionNumber, institutionId, careworkerId, guardianId);

        //when

        assertThatThrownBy(() -> new TestHelper(port)
            .user(Role.ADMIN, adminId, adminpassword)
            .uri("/admin/recipient")
            .requestBody(recipientRequest)
            .post()
            .toEntity(new ParameterizedTypeReference<ApiResult<RecipientResponse>>() {
            })).hasMessageContaining("404");

    }
}