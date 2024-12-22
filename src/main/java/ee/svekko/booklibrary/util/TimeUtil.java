package ee.svekko.booklibrary.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUtil {
    public static LocalDateTime maxDateTime() {
        return LocalDateTime.of(3000, 12, 31, 0, 0, 0);
    }
}
