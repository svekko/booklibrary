package ee.svekko.booklibrary.dto;

import java.time.LocalDateTime;

public interface BookResponseDto {
    int getId();

    String getTitle();

    Integer getStatusId();

    String getStatusName();

    Integer getBookUsedById();

    String getBookUsedByEmail();

    Integer getCreatedById();

    LocalDateTime getStatusValidTo();
}
