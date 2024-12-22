package ee.svekko.booklibrary.auth;

import ee.svekko.booklibrary.dto.JsonResponseDto;
import ee.svekko.booklibrary.dto.LoginRequestDto;
import ee.svekko.booklibrary.userdata.UserDataCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final SecurityContextHolderStrategy strategy;
    private final SecurityContextRepository repository;
    private final AuthManager authManager;
    private final UserDataCookieService userDataCookieService;

    @PostMapping("/login")
    public JsonResponseDto login(@Valid @RequestBody LoginRequestDto reqData, HttpServletRequest req, HttpServletResponse resp) {
        String email = reqData.getEmail();
        String passwd = reqData.getPassword();

        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(email, passwd);
        Authentication authentication = authManager.authenticate(token);
        SecurityContext securityContext = strategy.createEmptyContext();

        securityContext.setAuthentication(authentication);
        strategy.setContext(securityContext);
        repository.saveContext(securityContext, req, resp);
        userDataCookieService.setUserDataCookie(resp);

        return new JsonResponseDto();
    }

    @PostMapping("/logout")
    public JsonResponseDto logout(HttpServletRequest req, HttpServletResponse resp) {
        SecurityContextHolder.clearContext();
        HttpSession session = req.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        userDataCookieService.setUserDataCookie(resp);
        return new JsonResponseDto();
    }
}
