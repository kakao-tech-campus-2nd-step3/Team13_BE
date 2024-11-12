package dbdr.security.model;


import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class BaseUserDetails implements UserDetails {

    private final Long id;
    @NonNull
    private final String userLoginId;
    @NonNull
    private final Role role;
    private Long institutionId; //TODO : final 처리를 위해 BaseUserDetails 를 바꿔줘야함.

    private final String password;

    @Builder
    public BaseUserDetails(Long id, @NonNull String userLoginId, @NonNull Role role,
        String password) {
        this.id = id;
        this.userLoginId = userLoginId;
        this.role = role;
        this.password = password;
    }

    @Builder
    public BaseUserDetails(Long id, @NonNull String userLoginId, @NonNull Role role,
        String password, Long institutionId) {
        this(id, userLoginId, role, password);
        this.institutionId = institutionId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> String.valueOf(role));
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

    public boolean isAdmin(){
        return role.equals(Role.ADMIN);
    }

    public boolean isInstitution(){
        return role.equals(Role.INSTITUTION);
    }

    public boolean isCareworker(){
        return role.equals(Role.CAREWORKER);
    }

    public boolean isGuardian(){
        return role.equals(Role.GUARDIAN);
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }
}
