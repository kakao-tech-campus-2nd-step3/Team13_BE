package dbdr.security;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseUserDetailsService implements UserDetailsService {

    private final GuardianRepository guardianRepository;
    private final CareworkerRepository careWorkerRepository;


    @Override
    public BaseUserDetails loadUserByUsername(String username) {
        Role role;
        String userId;
        try{
            var spliter = username.split("_");
            userId = spliter[0];
            role = Role.valueOf(spliter[1]);


            if(role==Role.GUARDIAN){
                return getGuadianDetails(userId);
            }
            if(role==Role.ADMIN){
                return getAdminDetails(userId);
            }
            if(role==Role.CAREWORKER){
                return getCareWorkerDetails(userId);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다."); //TODO : 예외 처리핸들링 필요
        }
        return null; //TODO : 예외 처리핸들링 필요
    }

    private BaseUserDetails getCareWorkerDetails(String userId) {

        Careworker careWorker = careWorkerRepository.findByPhone(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .username(careWorker.getPhone())
            .password(careWorker.getLoginPassword())
            .role(Role.CAREWORKER.name())
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userDetails;
    }

    private BaseUserDetails getAdminDetails(String username) {
        //TODO : admin 필요
        /*
        Admin admin = adminRepository.findByPhone(username).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .username(admin.getPhone())
            .password(admin.getLoginPassword())
            .role(Role.ADMIN.name())
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userDetails;

         */
        return null;
    }

    private BaseUserDetails getGuadianDetails(String userId) {
        Guardian guardian = guardianRepository.findByPhone(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        BaseUserDetails userDetails = BaseUserDetails.builder()
            .username(guardian.getPhone())
            .password(guardian.getLoginPassword())
            .role(Role.GUARDIAN.name())
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userDetails;
    }

}
