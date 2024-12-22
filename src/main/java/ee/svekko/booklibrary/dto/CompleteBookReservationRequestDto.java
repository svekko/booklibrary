package ee.svekko.booklibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteBookReservationRequestDto {
    private Integer reservedToAccountId;
}
