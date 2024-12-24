package ee.svekko.booklibrary.model;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;

public record UserSession(int id) {
    public static UserSession fromSecurityContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication != null && authentication.getDetails() instanceof UserSession userSession) {
            return userSession;
        }

        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
    }
}
