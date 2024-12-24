package ee.svekko.booklibrary.service;

import ee.svekko.booklibrary.container.PostgreContainerTest;
import ee.svekko.booklibrary.dto.AddBookRequestDto;
import ee.svekko.booklibrary.dto.BookResponseDto;
import ee.svekko.booklibrary.error.InvalidDataError;
import ee.svekko.booklibrary.exception.InvalidDataException;
import ee.svekko.booklibrary.model.UserAccount;
import ee.svekko.booklibrary.repository.BookChangeRepository;
import ee.svekko.booklibrary.repository.BookRepository;
import ee.svekko.booklibrary.repository.BookStatusChangeRepository;
import ee.svekko.booklibrary.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BookLibraryServiceTest extends PostgreContainerTest {
    private static UserAccount userAccount1;
    private static UserAccount userAccount2;

    @Autowired
    private BookLibraryService bookLibraryService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookChangeRepository bookChangeRepository;

    @Autowired
    private BookStatusChangeRepository bookStatusChangeRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    void beforeEach() {
        bookStatusChangeRepository.deleteAll();
        bookChangeRepository.deleteAll();
        bookRepository.deleteAll();

        if (userAccount1 == null) {
            userAccount1 = userAccountRepository.save(UserAccount.builder()
                .email("test11@local.host")
                .password("password")
                .build());
        }

        if (userAccount2 == null) {
            userAccount2 = userAccountRepository.save(UserAccount.builder()
                .email("test12@local.host")
                .password("password")
                .build());
        }
    }

    @Test
    void testGetBooks() {
        List<BookResponseDto> dtos = bookLibraryService.getBooks("");
        assertThat(dtos.size()).isEqualTo(0);

        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");
        bookLibraryService.addBook(userAccount1, requestDto);

        dtos = bookLibraryService.getBooks("");
        assertThat(dtos.size()).isEqualTo(1);

        dtos = bookLibraryService.getBooks("es");
        assertThat(dtos.size()).isEqualTo(1);

        dtos = bookLibraryService.getBooks("Es");
        assertThat(dtos.size()).isEqualTo(1);

        dtos = bookLibraryService.getBooks("invalid");
        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void testAddBook() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");
        bookLibraryService.addBook(userAccount1, requestDto);

        requestDto.setTitle("test2");
        bookLibraryService.addBook(userAccount1, requestDto);

        List<BookResponseDto> dtos = bookLibraryService.getBooks("");
        assertThat(dtos.size()).isEqualTo(2);

        assertThatThrownBy(() -> bookLibraryService.addBook(userAccount1, requestDto))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_MUST_HAVE_UNIQUE_TITLE.getMessage());
    }

    @Test
    void testRemoveBook() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.removeBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.removeBook(userAccount1, bookId))
            .isOfAnyClassIn(EntityNotFoundException.class);
    }

    @Test
    void testBorrowBook() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.borrowBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.borrowBook(userAccount1, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_AVAILABLE.getMessage());
    }

    @Test
    void testReserveBookAndCompleteOwnReservation() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.reserveBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.reserveBook(userAccount1, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_AVAILABLE.getMessage());

        assertThatThrownBy(() -> bookLibraryService.completeBookReservation(userAccount2, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION.getMessage());

        bookLibraryService.completeBookReservation(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.completeBookReservation(userAccount1, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_RESERVED.getMessage());
    }

    @Test
    void testReserveBookAndCompleteOthersReservation() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.reserveBook(userAccount2, bookId);

        assertThatThrownBy(() -> bookLibraryService.reserveBook(userAccount2, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_AVAILABLE.getMessage());

        bookLibraryService.completeBookReservation(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.completeBookReservation(userAccount1, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_RESERVED.getMessage());
    }

    @Test
    void testReserveBookAndCancelOwnReservation() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.reserveBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.reserveBook(userAccount1, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_AVAILABLE.getMessage());

        assertThatThrownBy(() -> bookLibraryService.cancelBookReservation(userAccount2, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_RESERVED_FOR_SOMEONE_ELSE.getMessage());

        bookLibraryService.cancelBookReservation(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.cancelBookReservation(userAccount1, bookId))
            .isOfAnyClassIn(EntityNotFoundException.class);
    }

    @Test
    void testReserveBookAndCancelOthersReservation() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.reserveBook(userAccount2, bookId);

        assertThatThrownBy(() -> bookLibraryService.reserveBook(userAccount2, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_NOT_AVAILABLE.getMessage());

        bookLibraryService.cancelBookReservation(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.cancelBookReservation(userAccount1, bookId))
            .isOfAnyClassIn(EntityNotFoundException.class);
    }

    @Test
    void returnOwnBook() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.borrowBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.returnBook(userAccount2, bookId))
            .isOfAnyClassIn(InvalidDataException.class)
            .hasMessage(InvalidDataError.BOOK_BORROWED_BY_SOMEONE_ELSE.getMessage());

        bookLibraryService.returnBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.returnBook(userAccount1, bookId))
            .isOfAnyClassIn(EntityNotFoundException.class);
    }

    @Test
    void returnOthersBook() {
        AddBookRequestDto requestDto = new AddBookRequestDto();
        requestDto.setTitle("test");

        int bookId = bookLibraryService.addBook(userAccount1, requestDto);
        bookLibraryService.borrowBook(userAccount2, bookId);

        bookLibraryService.returnBook(userAccount1, bookId);

        assertThatThrownBy(() -> bookLibraryService.returnBook(userAccount1, bookId))
            .isOfAnyClassIn(EntityNotFoundException.class);
    }
}
