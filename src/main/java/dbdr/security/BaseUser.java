package dbdr.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor

public class BaseUser {

    private String username;
    private String password;
    private String role;

}
