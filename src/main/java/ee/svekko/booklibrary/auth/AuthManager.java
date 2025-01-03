package ee.svekko.booklibrary.auth;

import ee.svekko.booklibrary.model.UserAccount;
import ee.svekko.booklibrary.model.UserSession;
import ee.svekko.booklibrary.repository.UserAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthManager implements AuthenticationManager {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication auth) {
        String email = auth.getPrincipal() + "";
        String password = auth.getCredentials() + "";
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);

        if (userAccount == null) {
            throw new BadCredentialsException("User not found");
        }

        if (!encoder.matches(password, userAccount.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(
            auth.getPrincipal(),
            auth.getCredentials(),
            auth.getAuthorities());

        token.setDetails(new UserSession(userAccount.getId()));
        return token;
    }
}
