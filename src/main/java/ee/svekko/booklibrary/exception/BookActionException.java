package ee.svekko.booklibrary.exception;

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
        BOOK_NOT_RESERVED("Book not reserved"),
        BOOK_NOT_BORROWED("Book not borrowed"),
        BOOK_NOT_AVAILABLE("Book not available"),
        BOOK_RESERVED_FOR_SOMEONE_ELSE("Book reserved for someone else"),
        BOOK_BORROWED_BY_SOMEONE_ELSE("Book borrowed by someone else"),
        NOT_OWN_BOOK("Not own book"),
        CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION("Can complete only own book reservation");

        private final String message;
    }
}
