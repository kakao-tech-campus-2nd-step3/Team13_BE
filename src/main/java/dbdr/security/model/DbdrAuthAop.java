package dbdr.security.model;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.service.DbdrSeucrityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class DbdrAuthAop {

    private final DbdrSeucrityService dbdrSeucrityService;

    @Around("@annotation(DbdrAuth)")
    public Object authCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("권한검사 AOP 시작");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DbdrAuth dbdrAuth = signature.getMethod().getAnnotation(DbdrAuth.class);

        Role role = dbdrAuth.targetRole();
        AuthParam authParam = dbdrAuth.authParam();
        String id = dbdrAuth.id(); //아무 정보도 없으면 request body를 가져온다?
        if(!authParam.equals(AuthParam.NONE)){
            Object[] args = joinPoint.getArgs(); // 메소드의 파라미터를 가져오기
            id = args[0].toString();
        }

        if(!dbdrSeucrityService.hasAcesssPermission(role, authParam, id)) {
            log.info("권한이 없습니다.");
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        return joinPoint.proceed();
    }
}
