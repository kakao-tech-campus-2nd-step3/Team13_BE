package dbdr.domain.guardian.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/guardian")
@RequiredArgsConstructor
public class GuardianLoginController {

    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        String token = "token-test";
        return ResponseEntity.ok().header("Authorization", token).build();
    }
}
