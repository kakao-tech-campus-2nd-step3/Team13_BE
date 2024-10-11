package dbdr.security.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import org.junit.jupiter.api.Test;

class DbdrAcessTest {
    //인가과정을 담당하는 Model Test

    //관리자 권한을 가진 사용자가 모든 권한을 가지고 있는지 확인
    @Test
    void 관리자_이면_전부_ok() {
        //given
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .id(1L)
            .userLoginId("admin")
            .password("admin")
            .role("ADMIN")
            .institutionId(1L)
            .build();

        DbdrAcess dbdrAcess = new DbdrAcess();

        Guardian guardian = new Guardian("123", "123");
        Careworker careworker = new Careworker(1L, "123","ee","123");
        Institution institution = new Institution(1L, "123");
        //when
        boolean resultGuardian = dbdrAcess.hasAccessPermission(userDetails, guardian);
        boolean resultCareworker = dbdrAcess.hasAccessPermission(userDetails, careworker);
        boolean resultInstitution = dbdrAcess.hasAccessPermission(userDetails, institution);
        boolean resultChart  = dbdrAcess.hasAccessPermission(userDetails, new Chart());

        //then
        assertThat(resultGuardian).isTrue();
        assertThat(resultCareworker).isTrue();
        assertThat(resultInstitution).isTrue();
        assertThat(resultChart).isTrue();
    }
    @Test
    void Careworker는_Guardian_접근불가(){
        //given
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .id(1L)
            .userLoginId("careworker")
            .password("careworker")
            .role("CAREWORKER")
            .institutionId(1L)
            .build();

        DbdrAcess dbdrAcess = new DbdrAcess();

        Guardian guardian = new Guardian("123", "123");

        //when
        boolean resultGuardian = dbdrAcess.hasAccessPermission(userDetails, guardian);

        //then
        assertThat(resultGuardian).isFalse();
    }

    @Test
    void Careworker는_접근자가_요양원ID같아야_접근가능(){
        //given
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .id(1L)
            .userLoginId("careworker")
            .password("careworker")
            .role("CAREWORKER")
            .institutionId(1L)
            .build();

        DbdrAcess dbdrAcess = new DbdrAcess();

        Careworker careworker = new Careworker(1L, "123","ee","123");
        Careworker careworker2 = new Careworker(2L, "123","ee","123");

        //when
        boolean resultCareworker = dbdrAcess.hasAccessPermission(userDetails, careworker);
        boolean resultCareworker2 = dbdrAcess.hasAccessPermission(userDetails, careworker2);

        //then
        assertThat(resultCareworker).isTrue();
        assertThat(resultCareworker2).isFalse();
    }

}