package dbdr.security.model;

import static org.junit.jupiter.api.Assertions.*;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.entity.Guardian;
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

        Guardian guardian = Guardian.builder()
            .id(1L)
            .institutionId(1L)
            .build();

        //when
        boolean result = dbdrAcess.hasAccessPermission(userDetails, baseEntity);

        //then
        assertTrue(result);
    }
}