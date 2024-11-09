package dbdr.domain.admin.service;

import dbdr.domain.admin.AdminCreateRequest;
import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.repository.AdminRepository;
import dbdr.global.exception.ApplicationException;
import dbdr.global.exception.ApplicationError;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addAdmin(Admin admin) {
        log.info("관리자 존재 여부 확인 : {}",adminRepository.findAll().isEmpty());
        admin.changePassword(passwordEncoder.encode(admin.getLoginPassword()));
        adminRepository.save(admin);
    }

    @Transactional
    public void addAdmin(AdminCreateRequest adminCreateRequest) {
        Admin admin = Admin.builder()
                .loginId(adminCreateRequest.loginId())
                .loginPassword(passwordEncoder.encode(adminCreateRequest.loginPassword()))
                .build();
        addAdmin(admin);
    }

    @Transactional(readOnly = true)
    public String getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.DATABASE_ERROR));

        return admin.getLoginId();
    }

    @Transactional(readOnly = true)
    public List<String> getAllAdmins() {
        return adminRepository.findAll().stream().map(Admin::getLoginId).toList();
    }
}
