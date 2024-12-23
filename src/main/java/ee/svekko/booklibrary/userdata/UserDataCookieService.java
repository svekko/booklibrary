package ee.svekko.booklibrary.userdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.svekko.booklibrary.dto.UserAccountResponseDto;
import ee.svekko.booklibrary.model.UserAccount;
import ee.svekko.booklibrary.model.UserSession;
import ee.svekko.booklibrary.repository.UserAccountRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserDataCookieService {
    private final UserAccountRepository userAccountRepository;

    public void setUserDataCookie(HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserAccountResponseDto userAccountDto = userAccountResponseDtoFromAuthentication(authentication);

        byte[] userAccountBytes = userAccountDtoToJson(userAccountDto);

        Cookie cookie = new Cookie("user-data", Base64.getEncoder().encodeToString(userAccountBytes));
        cookie.setHttpOnly(false);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    private UserAccountResponseDto userAccountResponseDtoFromAuthentication(@Nullable Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof UserSession userSession) {
            UserAccount userAccount = userAccountRepository.findById(userSession.id()).orElse(null);
            if (userAccount != null) {
                return UserAccountResponseDto.builder()
                    .id(userAccount.getId())
                    .email(userAccount.getEmail())
                    .authenticated(true)
                    .build();
            }
        }

        return UserAccountResponseDto.builder()
            .authenticated(false)
            .build();
    }

    private byte[] userAccountDtoToJson(UserAccountResponseDto userAccountDto) {
        try {
            return new ObjectMapper().writeValueAsBytes(userAccountDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
