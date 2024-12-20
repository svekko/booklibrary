package ee.smit.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class JsonResponseDto {
    private String timestamp;

    @JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
    private String error;

    public static JsonResponseDto error(String error) {
        JsonResponseDto jsonResult = new JsonResponseDto();
        jsonResult.setError(error);
        jsonResult.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return jsonResult;
    }
}
