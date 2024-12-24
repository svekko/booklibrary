package ee.svekko.booklibrary.logging;

import ee.svekko.booklibrary.model.UserSession;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order
@Slf4j
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest servletRequest) {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();

            if (authentication.getDetails() instanceof UserSession userSession) {
                log.info("New {} request to {} (user id: {})", servletRequest.getMethod(), servletRequest.getRequestURI(), userSession.id());
            } else {
                log.info("New {} request to {}", servletRequest.getMethod(), servletRequest.getRequestURI());
            }
        }

        chain.doFilter(request, response);
    }
}
