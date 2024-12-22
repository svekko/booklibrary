package ee.svekko.booklibrary.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponseDto {
    private int id;
    private String title;
}
