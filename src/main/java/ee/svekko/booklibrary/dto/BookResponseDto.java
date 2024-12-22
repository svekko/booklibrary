package ee.svekko.booklibrary.dto;

public interface BookResponseDto {
    int getId();

    String getTitle();

    Integer getStatusId();

    String getStatusName();

    Integer getBookUsedById();
}
