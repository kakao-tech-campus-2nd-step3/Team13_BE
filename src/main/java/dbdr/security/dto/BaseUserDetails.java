package dbdr.security.dto;

import dbdr.security.BaseUser;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class BaseUserDetails implements UserDetails {

    private final BaseUser baseUser;

    public BaseUserDetails(BaseUser baseUser) {
        this.baseUser = baseUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) baseUser::getRole);

        return collection;
    }

    @Override
    public String getPassword() {
        //TODO: 비밀번호 반환
        return null;
    }
     @Override
    public String getUsername() {
        return null;
    }
}
