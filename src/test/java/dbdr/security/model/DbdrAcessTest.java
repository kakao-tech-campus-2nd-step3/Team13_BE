package dbdr.security.model;

import static org.assertj.core.api.Assertions.assertThat;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DbdrAcessTest {
    //인가과정을 담당하는 Model Test

    //관리자는 모든 유형에 대해 접근 권한을 가진다.
    @Test
    @DisplayName("관리자는 모든 유형 접근 가능")
    void admin_has_access_permission() {
        //given
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 관리자
        BaseUserDetails userDetails = new BaseUserDetails(1L,"LoginId",Role.ADMIN,"password");

        //접근 테스트 대상
        Institution institution = Institution.builder().build();
        Institution institution1 = Institution.builder().institutionName("test").institutionNumber(3L).build();
        Careworker careworker = Careworker.builder().build();
        Guardian guardian = Guardian.builder().build();
        //when
        boolean institutionResult = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, institution);
        boolean institutionResult1 = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, institution1);
        boolean careworkerResult = dbdrAcess.hasAccessPermission(Role.CAREWORKER, userDetails, careworker);
        boolean guardianResult = dbdrAcess.hasAccessPermission(Role.GUARDIAN, userDetails, guardian);

        //then
        assertThat(institutionResult).isTrue();
        assertThat(institutionResult1).isTrue();
        assertThat(careworkerResult).isTrue();
        assertThat(guardianResult).isTrue();
    }

    @Test
    @DisplayName("요양원은 자신의 소속 요양보호사만 접근할 수 있다.")
    void institution_has_access_permission() {
        //given
        DbdrAcess dbdrAcess = new DbdrAcess();

        //접근한 사용자는 요양원
        BaseUserDetails userDetails = new BaseUserDetails(1L,"LoginId",Role.INSTITUTION,"password",1L);

        //접근 테스트 대상
        //Careworker careworker = Careworker.builder().institutionId(1L).build(); //같은 요양원 소속 요양보호사
        //Careworker careworker2 = Careworker.builder().institutionId(2L).build(); //타 기관 소속 요양보호사

        //when
        //boolean careworkerResult = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, careworker);
        //boolean careworkerResult2 = dbdrAcess.hasAccessPermission(Role.INSTITUTION, userDetails, careworker2);

        //then
        //assertThat(careworkerResult).isTrue();
        //assertThat(careworkerResult2).isFalse();
    }



}
