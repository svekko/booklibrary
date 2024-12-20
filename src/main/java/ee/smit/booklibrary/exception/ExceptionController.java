package ee.smit.booklibrary.exception;

import ee.smit.booklibrary.dto.JsonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ExceptionController {
    @ExceptionHandler(BookActionException.class)
    public ResponseEntity<JsonResponseDto> handleBookActionException(BookActionException e) {
        log.error(e.getError().getMessage());
        JsonResponseDto responseDto = JsonResponseDto.error(e.getError().getMessage());
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResponseDto> handleException(Exception e) {
        log.error(e.getMessage());
        JsonResponseDto responseDto = JsonResponseDto.error("systemError");
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
