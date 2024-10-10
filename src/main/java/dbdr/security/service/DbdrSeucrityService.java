package dbdr.security.service;

import dbdr.security.Role;
import dbdr.security.dto.BaseUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DbdrSeucrityService {
    
    public boolean hasRole(String role, String institutionId) {

        if(Role.valueOf(role) == Role.ADMIN){
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        BaseUserDetails baseUserDetails = (BaseUserDetails) authentication.getPrincipal();

        //같은 요양원 번호에서는 접근 가능
        if(baseUserDetails.getInstitutionNumber() == Long.parseLong(institutionId)){
            //TODO : careworker일 경우 본인 담당만 조회 ok
            if(Role.valueOf(role) == Role.INSTITUTION){
                return true;
            }
        }

        return false;
    }
}
