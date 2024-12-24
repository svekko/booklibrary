package ee.svekko.booklibrary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddBookRequestDto {
    @NotBlank
    private String title;
}
