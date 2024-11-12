package dbdr.e2etest.Admin;

import static org.assertj.core.api.Assertions.assertThat;

import dbdr.domain.admin.entity.Admin;
import dbdr.security.model.Role;
import dbdr.testhelper.TestHelper;
import dbdr.testhelper.TestHelperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestHelperFactory testHelperFactory;


    @Test
    @DisplayName("기존 관리자 계정 전체 조회")
    public void getAdminTest(){
        //given
        Admin admin = Admin.builder().loginId("ad").loginPassword("123").build();
        Admin admin2 = Admin.builder().loginId("ad2").loginPassword("123").build();

        testHelperFactory.addAdmin(admin);
        testHelperFactory.addAdmin(admin2);

        TestHelper testHelper = testHelperFactory.create(port);

        //when
        var response = testHelper.user(Role.ADMIN,"ad","123").uri("/admin").get().toEntity(String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("ad","ad2");

    }


}
