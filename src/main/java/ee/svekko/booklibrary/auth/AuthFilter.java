package ee.svekko.booklibrary.auth;

import ee.svekko.booklibrary.userdata.UserDataCookieService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order
@RequiredArgsConstructor
public class AuthFilter implements Filter {
    private final UserDataCookieService userDataCookieService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            userDataCookieService.setUserDataCookie((HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }
}
