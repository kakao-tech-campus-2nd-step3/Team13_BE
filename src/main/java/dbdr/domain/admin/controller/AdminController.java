package dbdr.domain.admin.controller;

import dbdr.domain.admin.AdminCreateRequest;
import dbdr.domain.admin.service.AdminService;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[관리자] 서버관리자 (Admin)", description = "서버관리자 정보 조회, 수정")
@RestController
@RequestMapping("/${spring.app.version}/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "서버관리자 정보 조회",security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    @DbdrAuth(targetRole = Role.ADMIN)
    public ResponseEntity<List<String>> getAdminList(){
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @Operation(summary = "특정 서버관리자 정보 조회",security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{id}")
    @DbdrAuth(targetRole = Role.ADMIN, authParam = AuthParam.ADMIN_ID,id = "#id")
    public ResponseEntity<String> getAdmin(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    @Operation(summary = "서버관리자 추가")
    @PostMapping("/add")
    public ResponseEntity<Void> addAdmin(@RequestBody AdminCreateRequest adminCreateRequest){

        log.info("어드민 생성 시작, {} {}", adminCreateRequest.loginId(), adminCreateRequest.loginPassword());
        adminService.addAdmin(adminCreateRequest);
        return ResponseEntity.ok().build();
    }
}
