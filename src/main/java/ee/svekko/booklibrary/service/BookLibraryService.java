package ee.svekko.booklibrary.service;

import ee.svekko.booklibrary.config.BookLibraryConfig;
import ee.svekko.booklibrary.dto.AddBookRequestDto;
import ee.svekko.booklibrary.dto.BookResponseDto;
import ee.svekko.booklibrary.dto.CompleteBookReservationRequestDto;
import ee.svekko.booklibrary.exception.BookActionException;
import ee.svekko.booklibrary.model.*;
import ee.svekko.booklibrary.repository.BookChangeRepository;
import ee.svekko.booklibrary.repository.BookRepository;
import ee.svekko.booklibrary.repository.BookStatusChangeRepository;
import ee.svekko.booklibrary.util.TimeUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLibraryService {
    private final BookStatusChangeRepository bookStatusChangeRepository;
    private final BookChangeRepository bookChangeRepository;
    private final BookRepository bookRepository;
    private final BookLibraryConfig bookLibraryConfig;

    public List<BookResponseDto> getBooks() {
        return bookChangeRepository.getBooks()
            .stream()
            .map(BookChange::toBookResponseDto)
            .toList();
    }

    public List<BookResponseDto> getOwnBooks(UserAccount userAccount) {
        return bookChangeRepository.getBooksWhereChangedBy(userAccount.getId())
            .stream()
            .map(BookChange::toBookResponseDto)
            .toList();
    }

    @Transactional
    public void addBook(UserAccount userAccount, AddBookRequestDto requestDto) {
        Book book = bookRepository.save(Book.builder()
            .build());

        bookChangeRepository.save(BookChange.builder()
            .book(book)
            .title(requestDto.getTitle())
            .changedBy(userAccount)
            .validFrom(LocalDateTime.now())
            .validTo(TimeUtil.maxDateTime())
            .build());
    }

    @Transactional
    public void removeBook(UserAccount userAccount, int bookId) {
        BookChange bookChg = bookChangeRepository
            .getBookByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        if (!bookChg.getChangedBy().getId().equals(userAccount.getId())) {
            throw new BookActionException(BookActionException.Error.NOT_OWN_BOOK);
        }

        LocalDateTime now = LocalDateTime.now();

        bookChg.setValidTo(now);
        bookChangeRepository.save(bookChg);

        bookChangeRepository.save(BookChange.builder()
            .book(bookChg.getBook())
            .title(bookChg.getTitle())
            .changedBy(userAccount)
            .validFrom(now)
            .validTo(now)
            .build());
    }

    @Transactional
    public void reserveBook(UserAccount userAccount, int bookId) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(bookId)
            .orElse(null);

        if (bookStatusChg != null) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_AVAILABLE);
        }

        LocalDateTime validTo = LocalDateTime.now().plus(bookLibraryConfig.getBookHoursReserved(), ChronoUnit.HOURS);
        addBookStatus(bookId, BookStatusValue.RESERVED, userAccount, userAccount, validTo);
    }

    @Transactional
    public void borrowBook(UserAccount userAccount, int bookId) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(bookId)
            .orElse(null);

        if (bookStatusChg != null) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_AVAILABLE);
        }

        LocalDateTime validTo = LocalDateTime.now().plus(bookLibraryConfig.getBookDaysBorrowed(), ChronoUnit.DAYS);
        addBookStatus(bookId, BookStatusValue.BORROWED, userAccount, userAccount, validTo);
    }

    @Transactional
    public void completeBookReservation(UserAccount userAccount, @Nullable CompleteBookReservationRequestDto requestDto, int bookId) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        UserAccount reservedTo = UserAccount.builder()
            .id(requestDto != null ? requestDto.getReservedToAccountId() : userAccount.getId())
            .build();

        if (!BookStatusValue.RESERVED.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_RESERVED);
        }

        if (!userAccount.getId().equals(reservedTo.getId()) && !isOwnBook(userAccount, bookId)) {
            throw new BookActionException(BookActionException.Error.CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validTo = now.plus(bookLibraryConfig.getBookDaysBorrowed(), ChronoUnit.DAYS);

        bookStatusChg.setValidTo(now);
        bookStatusChangeRepository.save(bookStatusChg);

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(bookStatusChg.getBook())
            .bookStatus(BookStatus.builder().id(BookStatusValue.BORROWED.getId()).build())
            .bookUsedBy(reservedTo)
            .changedBy(userAccount)
            .validFrom(now)
            .validTo(validTo)
            .build());
    }

    @Transactional
    public void cancelBookReservation(UserAccount userAccount, int bookId) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        UserAccount bookUsedBy = bookStatusChg.getBookUsedBy();

        if (!BookStatusValue.RESERVED.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_RESERVED);
        }

        if (!bookUsedBy.getId().equals(userAccount.getId()) && !isOwnBook(userAccount, bookId)) {
            throw new BookActionException(BookActionException.Error.BOOK_RESERVED_FOR_SOMEONE_ELSE);
        }

        removeBookStatus(bookStatusChg);
    }

    @Transactional
    public void returnBook(UserAccount userAccount, int bookId) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        UserAccount bookUsedBy = bookStatusChg.getBookUsedBy();

        if (!BookStatusValue.BORROWED.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_BORROWED);
        }

        if (!bookUsedBy.getId().equals(userAccount.getId()) && !isOwnBook(userAccount, bookId)) {
            throw new BookActionException(BookActionException.Error.BOOK_BORROWED_BY_SOMEONE_ELSE);
        }

        removeBookStatus(bookStatusChg);
    }

    private boolean isOwnBook(UserAccount userAccount, int bookId) {
        BookChange bookChg = bookChangeRepository
            .getBookByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        return bookChg.getChangedBy().getId().equals(userAccount.getId());
    }

    private void addBookStatus(int bookId, BookStatusValue newStatus, UserAccount bookUsedBy, UserAccount changedBy, LocalDateTime validTo) {
        BookChange bookChg = bookChangeRepository
            .getBookByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(bookChg.getBook())
            .bookStatus(BookStatus.builder().id(newStatus.getId()).build())
            .bookUsedBy(bookUsedBy)
            .changedBy(changedBy)
            .validFrom(LocalDateTime.now())
            .validTo(validTo)
            .build());
    }

    private void removeBookStatus(BookStatusChange bookStatusChg) {
        bookStatusChg.setValidTo(LocalDateTime.now());
        bookStatusChangeRepository.save(bookStatusChg);
    }
}
