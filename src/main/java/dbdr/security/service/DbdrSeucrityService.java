package dbdr.security.service;

import dbdr.domain.admin.repository.AdminRepository;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAcess;
import dbdr.security.model.Role;
import dbdr.security.model.BaseUserDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class DbdrSeucrityService {

    private final AdminRepository adminRepository;
    private final InstitutionRepository institutionRepository;
    private final CareworkerRepository careworkerRepository;
    private final GuardianRepository guardianRepository;
    private final ChartRepository chartRepository;
    private final RecipientRepository recipientRepository;

    private final DbdrAcess dbdrAcess;

    public boolean hasAcesssPermission(@NotNull Role role, @NotNull AuthParam authParam,String id) {
        log.info("권한확인 메소드 동작 시작 : role : {}, authParam : {}, id : {}", role, authParam, id);

        BaseUserDetails baseUserDetails;

        Object principal = SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        if(principal instanceof BaseUserDetails){
            baseUserDetails = (BaseUserDetails) principal;
        } else {
            log.info("로그인 되지 않은 사용자의 접근입니다.");
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        if (authParam.equals(AuthParam.NONE)) {
            return dbdrAcess.hasRole(role, baseUserDetails);
        }

        if(authParam.equals(AuthParam.LOGIN_INSTITUTION)){ //요양원으로 접근한 경우, 본인 요양원 정보만 접근해야함.
            return dbdrAcess.hasAccessPermission(role,baseUserDetails,
                findEntity(AuthParam.INSTITUTION_ID, baseUserDetails.getInstitutionId()));
        }
        if(authParam.equals(AuthParam.LOGIN_CAREWORKER)){ //요양사로 접근한 경우, 본인 요양사 정보만 접근해야함.
            return dbdrAcess.hasAccessPermission(role,baseUserDetails,
                findEntity(AuthParam.CAREWORKER_ID, baseUserDetails.getId()));
        }
        if(authParam.equals(AuthParam.LOGIN_GUARDIAN)){ //보호자로 접근한 경우, 본인 보호자 정보만 접근해야함.
            return dbdrAcess.hasAccessPermission(role,baseUserDetails,
                findEntity(AuthParam.GUARDIAN_ID, baseUserDetails.getId()));
        }
        return dbdrAcess.hasAccessPermission(role,baseUserDetails, findEntity(authParam, Long.parseLong(id)));

    }

    private BaseEntity findEntity(AuthParam type, long id) {
        return switch (type) {
            case ADMIN_ID -> adminRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.ADMIN_NOT_FOUND));
            case INSTITUTION_ID -> institutionRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
            case CAREWORKER_ID -> careworkerRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
            case GUARDIAN_ID -> guardianRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));
            case CHART_ID -> chartRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.CHART_NOT_FOUND));
            case RECIPIENT_ID -> recipientRepository.findById(id).orElseThrow(() ->
                new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
            default -> throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        };
    }

}
