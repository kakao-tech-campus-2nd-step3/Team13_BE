package dbdr.security.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "유저 ID는 필수 항목입니다.") String userId,
                           @NotBlank(message = "비밀번호는 필수 항목입니다.") String password) {

}
