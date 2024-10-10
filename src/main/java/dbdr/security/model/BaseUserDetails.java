package dbdr.security.model;

import dbdr.security.Role;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Builder
@Getter
public class BaseUserDetails implements UserDetails {

    private final Long id; //db pk
    private final String userLoginId; //로그인 시 사용할 id
    private final String password;
    private final String role; //권한
    private final Long institutionId;

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
        return userLoginId;
    }

    public Role getRole() {
        return Role.valueOf(role);
    }
}
