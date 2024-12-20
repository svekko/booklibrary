package ee.smit.booklibrary.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BookActionException extends RuntimeException {
    private final Error error;

    public BookActionException(Error error) {
        super(error.message);
        this.error = error;
    }

    @AllArgsConstructor
    @Getter
    public enum Error {
        BOOK_NOT_RESERVED("bookNotReserved"),
        BOOK_NOT_BORROWED("bookNotBorrowed"),
        BOOK_NOT_AVAILABLE("bookNotAvailable"),
        BOOK_RESERVED_FOR_SOMEONE_ELSE("bookReservedForSomeoneElse"),
        BOOK_BORROWED_BY_SOMEONE_ELSE("bookBorrowedBySomeoneElse"),
        NOT_OWN_BOOK("notOwnBook"),
        CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION("canCompleteOnlyOwnBookReservation");

        private final String message;
    }
}
