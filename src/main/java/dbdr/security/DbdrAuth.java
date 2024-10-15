package dbdr.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@dbdrSecurityService.hasRole(#role, #institutionId)")
public @interface DbdrAuth {
    String role() default "";
    String institutionId() default "";
}
