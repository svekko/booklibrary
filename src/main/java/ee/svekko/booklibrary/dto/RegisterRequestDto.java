package ee.svekko.booklibrary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirm;
}
