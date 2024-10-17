package dbdr.security.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.dto.BaseUserDetails;
import dbdr.security.Role;
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

    public BaseUserDetails loadUserByUsernameAndRole(String username, Role role) {

        if (role == Role.GUARDIAN) {
            return getGuardianDetails(username);
        }
        if (role == Role.CAREWORKER) {
            return getCareWorkerDetails(username);
        }
        if (role == Role.INSTITUTION) {
            return getInstitutionDetails(username);
        }
        if (role == Role.ADMIN) {
            return getAdminDetails(username);
        }

        throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
    }

    private BaseUserDetails getInstitutionDetails(String userId) {
        if (!institutionRepository.existsByInstitutionNumber(Long.parseLong(userId))) {
            throw new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND);
        }

        Institution institution = institutionRepository.findByInstitutionNumber(
            Long.parseLong(userId));

        return securityRegister(institution.getId(), institution.getInstitutionNumber().toString(),
            institution.getLoginPassword(), Role.INSTITUTION);
    }

    private BaseUserDetails getCareWorkerDetails(String userId) {

        Careworker careWorker = careWorkerRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
        return securityRegister(careWorker.getId(), careWorker.getPhone(),
            careWorker.getLoginPassword(), Role.CAREWORKER);
    }

    private BaseUserDetails getAdminDetails(String username) {
        //TODO : admin 필요

        return null;
    }

    private BaseUserDetails getGuardianDetails(String userId) {
        log.debug("보호자 userId : {}", userId);
        Guardian guardian = guardianRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));

        return securityRegister(guardian.getId(), guardian.getPhone(), guardian.getLoginPassword(),
            Role.GUARDIAN);
    }

    private BaseUserDetails securityRegister(Long id, String username, String password, Role role) {
        BaseUserDetails userDetails = BaseUserDetails.builder().id(id).userLoginId(username)
            .password(password).role(role.name()).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
            userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userDetails;
    }

}
