package dbdr.security.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DbdrAuth 도메인 관련 권한 확인 어노테이션 <br>
 * <br>
 * targetRole : 누가 접근할 수 있는가 -> 어드민을 위한 것이다 or 요양보호사를 위한 것이다<br>
 * authParam : 해당 메소드 파라미터 타입 ex) 요양원ID, 요양사ID, 환자ID, 차트ID 등<br>
 * id : 전달하고자하는 실제 값의 메소드 파라미터명<br>
 * authParam이 없는 경우 targetRole만 있으면 ok입니다.<br>
 * <br>
 * Login ~~ 를 사용해서 로그인된 사용자 정보를 사용하는 경우 Authparam 에서 LOGIN_~~~ 를 사용합니다.<br>
 * DbdrAuth(targetRole = Role.GUARDIAN, authParam = AuthParam.LOGIN_GUARDIAN)<br>
 * <br>
 *
 * @see dbdr.security.service.DbdrSeucrityService
 * @see AuthParam
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbdrAuth {
    Role targetRole();
    AuthParam authParam() default AuthParam.NONE;
    String id() default "";
}
