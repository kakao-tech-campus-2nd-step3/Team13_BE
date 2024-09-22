package dbdr.testController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    //로그인 없이 조회 가능
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/nono")
    public String nono() {
        return "nono";
    }

    //로그인이 성공하면 sysout이 찍히는 메소드
    @GetMapping("/loginSuccess")
    @PreAuthorize("hasRole('GUARDIAN')")
    public String loginSuccess() {
        System.out.println("보호자 화면 성공");
        return "보호자 화면 성공";
    }

    //로그인이 성공하면 sysout이 찍히는 메소드
    @GetMapping("/loginSuccess2")
    @PreAuthorize("hasRole('CAREWORKER')")
    public String loginSuccess2() {
        System.out.println("요양보호사 화면 성공");
        return "요양보호사 화면 성공";
    }
}
