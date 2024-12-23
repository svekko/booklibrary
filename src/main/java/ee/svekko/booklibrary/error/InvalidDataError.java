package ee.svekko.booklibrary.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvalidDataError {
    BOOK_NOT_RESERVED("Book not reserved"),
    BOOK_NOT_BORROWED("Book not borrowed"),
    BOOK_NOT_AVAILABLE("Book not available"),
    BOOK_RESERVED_FOR_SOMEONE_ELSE("Book reserved for someone else"),
    BOOK_BORROWED_BY_SOMEONE_ELSE("Book borrowed by someone else"),
    NOT_OWN_BOOK("Not own book"),
    BOOK_MUST_HAVE_UNIQUE_TITLE("Book must have a unique title"),
    CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION("Can complete only own book reservation"),
    PASSWORDS_MUST_BE_SAME("Passwords must have same value"),
    USER_WITH_SUCH_EMAIL_EXISTS("User with such email already exists");

    private final String message;
}
