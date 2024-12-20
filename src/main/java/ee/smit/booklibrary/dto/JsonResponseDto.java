package ee.smit.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class JsonResponseDto {
    private String timestamp;
    private boolean ok;

    @JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
    private String error;

    public JsonResponseDto() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.ok = true;
    }

    public static JsonResponseDto error(String error) {
        JsonResponseDto jsonResult = new JsonResponseDto();
        jsonResult.ok = false;
        jsonResult.error = error;
        jsonResult.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        return jsonResult;
    }
}
