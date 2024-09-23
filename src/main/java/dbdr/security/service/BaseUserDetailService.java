package dbdr.security.service;

import dbdr.security.BaseUser;
import dbdr.security.dto.BaseUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseUserDetailService implements UserDetailsService {

    @Override
    public BaseUserDetails loadUserByUsername(String username) {

        BaseUser baseUser = new BaseUser();
        //TODO : DB에서 유저 정보를 가져와서 BaseUserDetails를 만들어서 리턴
        return new BaseUserDetails(baseUser);
    }

}
