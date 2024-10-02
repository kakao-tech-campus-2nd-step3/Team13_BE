package dbdr.security.dto;

import dbdr.security.Role;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Builder
@Slf4j
public class BaseUserDetails implements UserDetails {

    private final Long id; //db pk
    private final String username; //로그인 시 사용할 id
    private final String password;
    private final String role; //권한

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> role);
        return collection;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Role getRole(){
        return Role.valueOf(role);
    }

    public Long getId(){
        //TODO : NULL CHECK
        return id;
    }

}
