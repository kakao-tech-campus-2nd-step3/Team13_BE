package dbdr.security;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseUserDetailsService implements UserDetailsService {

    private final GuardianRepository guardianRepository;


    @Override
    public BaseUserDetails loadUserByUsername(String username) {

        Guardian guardian = guardianRepository.findByPhone(username).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        return new BaseUserDetails(guardian.getPhone(), guardian.getLoginPassword(), Role.GUARDIAN.name());
    }

}
