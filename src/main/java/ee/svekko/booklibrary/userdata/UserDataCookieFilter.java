package ee.svekko.booklibrary.userdata;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order
@RequiredArgsConstructor
public class UserDataCookieFilter implements Filter {
    private final UserDataCookieService userDataCookieService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse servletResponse) {
            userDataCookieService.setUserDataCookie(servletResponse);
        }

        chain.doFilter(request, response);
    }
}
