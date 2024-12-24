package ee.svekko.booklibrary.auth;

import ee.svekko.booklibrary.dto.JsonResponseDto;
import ee.svekko.booklibrary.dto.LoginRequestDto;
import ee.svekko.booklibrary.dto.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JsonResponseDto login(@Valid @RequestBody LoginRequestDto reqData, HttpServletRequest req, HttpServletResponse resp) {
        authService.login(reqData.getEmail(), reqData.getPassword(), req, resp);
        return new JsonResponseDto();
    }

    @PostMapping("/register")
    public JsonResponseDto register(@Valid @RequestBody RegisterRequestDto reqData) {
        authService.register(reqData.getEmail(), reqData.getPassword(), reqData.getPasswordConfirm());
        return new JsonResponseDto();
    }

    @PostMapping("/logout")
    public JsonResponseDto logout(HttpServletRequest req, HttpServletResponse resp) {
        authService.logout(req, resp);
        return new JsonResponseDto();
    }
}
