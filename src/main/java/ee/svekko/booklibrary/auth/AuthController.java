package ee.svekko.booklibrary.auth;

import ee.svekko.booklibrary.dto.JsonResponseDto;
import ee.svekko.booklibrary.dto.LoginRequestDto;
import ee.svekko.booklibrary.dto.RegisterRequestDto;
import ee.svekko.booklibrary.exception.BadRequestException;
import ee.svekko.booklibrary.model.UserAccount;
import ee.svekko.booklibrary.repository.UserAccountRepository;
import ee.svekko.booklibrary.userdata.UserDataCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/register")
    public JsonResponseDto register(@Valid @RequestBody RegisterRequestDto reqData) {
        String passwd = reqData.getPassword();
        String passwdConfirm = reqData.getPasswordConfirm();

        if (!StringUtils.equals(passwd, passwdConfirm)) {
            throw new BadRequestException("Passwords must have same value");
        }

        String email = reqData.getEmail();

        if (userAccountRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("User with such email already exists");
        }

        userAccountRepository.save(UserAccount.builder()
            .email(email)
            .password(passwordEncoder.encode(passwd))
            .build());

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
