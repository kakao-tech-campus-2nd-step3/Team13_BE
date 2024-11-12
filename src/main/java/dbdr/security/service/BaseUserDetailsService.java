package dbdr.security.service;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.repository.AdminRepository;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.model.BaseUserDetails;
import dbdr.security.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseUserDetailsService {

    private final GuardianRepository guardianRepository;
    private final CareworkerRepository careWorkerRepository;
    private final InstitutionRepository institutionRepository;
    private final AdminRepository adminRepository;

    public BaseUserDetails loadUserByUsernameAndRole(String username, Role role) {

        return switch (role) {
            case GUARDIAN -> getGuadianDetails(username);
            case CAREWORKER -> getCareWorkerDetails(username);
            case INSTITUTION -> getInstitutionDetails(username);
            case ADMIN -> getAdminDetails(username);
            default -> throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
        };

    }

    private BaseUserDetails getInstitutionDetails(String userId) {
        if (!institutionRepository.existsByLoginId(userId)) {
            log.info("해당 로그인 ID는 요양원 table에 존재하지 않습니다. id : {}", userId);
            throw new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND);
        }

        Institution institution = institutionRepository.findByLoginId(userId);

        return securityRegister(institution.getId(), institution.getLoginId(),
            institution.getLoginPassword(), Role.INSTITUTION, institution.getId());
    }

    private BaseUserDetails getCareWorkerDetails(String userId) {

        Careworker careWorker = careWorkerRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
        return securityRegister(careWorker.getId(), careWorker.getPhone(),
            careWorker.getLoginPassword(), Role.CAREWORKER,careWorker.getInstitution().getId());
    }

    private BaseUserDetails getAdminDetails(String username) {
        Admin admin = adminRepository.findByLoginId(username)
            .orElseThrow(() -> new ApplicationException(ApplicationError.ADMIN_NOT_FOUND));
        log.info("관리자 userId : {}", admin.getLoginId());
        log.info("관리자 password : {}", admin.getLoginPassword());
        return securityRegister(admin.getId(), admin.getLoginId(), admin.getLoginPassword(), Role.ADMIN);
    }

    private BaseUserDetails getGuadianDetails(String userId) {
        log.info("보호자 userId : {}", userId);
        Guardian guardian = guardianRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));

        return securityRegister(guardian.getId(), guardian.getPhone(), guardian.getLoginPassword(),
            Role.GUARDIAN, guardian.getInstitution().getId());
    }

    private BaseUserDetails securityRegister(Long id, String username, String password, Role role) {
        BaseUserDetails userDetails = BaseUserDetails.builder().id(id).userLoginId(username)
            .password(password).role(role).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
            userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("security register : {}", userDetails.getUserLoginId());
        return userDetails;
    }

    private BaseUserDetails securityRegister(Long id,String username, String password,Role role,Long institutionId){
        BaseUserDetails baseUserDetails = securityRegister(id,username,password,role);
        baseUserDetails.setInstitutionId(institutionId);
        log.info("security register : {}", baseUserDetails.getUserLoginId());
        return baseUserDetails;
    }

}
