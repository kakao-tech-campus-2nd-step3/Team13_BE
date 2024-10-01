package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.service.CareworkerLoginService;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${spring.app.version}/careworker")
public class CareworkerLoginController {

     private final CareworkerLoginService CareworkerLoginService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid CareworkerRequestDTO careworkerRequestDTO) {
        String token = GudiandLoginService.login(guardianRequest);
        return ResponseEntity.ok().header("Authorization", token).build();
    }
}
