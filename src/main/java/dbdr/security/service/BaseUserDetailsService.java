package dbdr.security.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.exception.ApplicationError;
import dbdr.exception.ApplicationException;
import dbdr.security.dto.BaseUserDetails;
import dbdr.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseUserDetailsService {

    private final GuardianRepository guardianRepository;
    private final CareworkerRepository careWorkerRepository;

    /*
    @Override
    public BaseUserDetails loadUserByUsername(String username) {

        var spliter = username.split("_");
        String userId = spliter[0];
        Role role = Role.valueOf(spliter[1].toUpperCase());

        if (role == Role.GUARDIAN) {
            return getGuadianDetails(userId);
        }
        if (role == Role.ADMIN) {
            return getAdminDetails(userId);
        }
        if (role == Role.CAREWORKER) {
            return getCareWorkerDetails(userId);
        }

        throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
    }

     */

    public BaseUserDetails loadUserByUsernameAndRole(String username, Role role) {
        if (role == Role.GUARDIAN) {
            log.debug("보호자 username : {}",username);
            return getGuadianDetails(username);
        }
        if (role == Role.ADMIN) {
            return getAdminDetails(username);
        }
        if (role == Role.CAREWORKER) {
            return getCareWorkerDetails(username);
        }

        throw new ApplicationException(ApplicationError.ROLE_NOT_FOUND);
    }

    private BaseUserDetails getCareWorkerDetails(String userId) {

        Careworker careWorker = careWorkerRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
        return securityRegister(careWorker.getId(),careWorker.getPhone(), careWorker.getLoginPassword(), Role.CAREWORKER);
    }

    private BaseUserDetails getAdminDetails(String username) {
        //TODO : admin 필요

        return null;
    }

    private BaseUserDetails getGuadianDetails(String userId) {
        log.debug("보호자 userId : {}",userId);
        Guardian guardian = guardianRepository.findByPhone(userId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.GUARDIAN_NOT_FOUND));

        return securityRegister(guardian.getId(),guardian.getPhone(), guardian.getLoginPassword(), Role.GUARDIAN);
    }

    private BaseUserDetails securityRegister(Long id,String username,String password,Role role) {
        BaseUserDetails userDetails = BaseUserDetails.builder().id(id).username(username)
            .password(password).role(role.name()).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
            userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userDetails;
    }

}
