package dbdr.domain.guardian.controller;

import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.service.GuardianLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/guardian")
@RequiredArgsConstructor
public class GuardianLoginController {

    private final GuardianLoginService GudiandLoginService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody GuardianRequest guardianRequest) {
        String token = GudiandLoginService.login(guardianRequest);
        return ResponseEntity.ok().header("Authorization", token).build();
    }
}