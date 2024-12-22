package ee.svekko.booklibrary.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BookLibraryConfig {
    @Value("${book_hours_reserved}")
    private int bookHoursReserved;

    @Value("${book_days_borrowed}")
    private int bookDaysBorrowed;
}
