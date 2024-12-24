package ee.svekko.booklibrary.auth;

import ee.svekko.booklibrary.error.InvalidDataError;
import ee.svekko.booklibrary.exception.InvalidDataException;
import ee.svekko.booklibrary.model.UserAccount;
import ee.svekko.booklibrary.repository.UserAccountRepository;
import ee.svekko.booklibrary.userdata.UserDataCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final SecurityContextHolderStrategy strategy;
    private final SecurityContextRepository repository;
    private final AuthManager authManager;
    private final UserDataCookieService userDataCookieService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(String email, String passwd, HttpServletRequest req, HttpServletResponse resp) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(email, passwd);
        Authentication authentication = authManager.authenticate(token);
        SecurityContext securityContext = strategy.createEmptyContext();

        securityContext.setAuthentication(authentication);
        strategy.setContext(securityContext);
        repository.saveContext(securityContext, req, resp);
        userDataCookieService.setUserDataCookie(resp);
    }

    public void register(String email, String passwd, String passwdConfirm) {
        if (!StringUtils.equals(passwd, passwdConfirm)) {
            throw new InvalidDataException(InvalidDataError.PASSWORDS_MUST_BE_SAME);
        }

        if (userAccountRepository.findByEmail(email).isPresent()) {
            throw new InvalidDataException(InvalidDataError.USER_WITH_SUCH_EMAIL_EXISTS);
        }

        userAccountRepository.saveAndFlush(UserAccount.builder()
            .email(email)
            .password(passwordEncoder.encode(passwd))
            .build());
    }

    public void logout(HttpServletRequest req, HttpServletResponse resp) {
        SecurityContextHolder.clearContext();
        HttpSession session = req.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        userDataCookieService.setUserDataCookie(resp);
    }
}
