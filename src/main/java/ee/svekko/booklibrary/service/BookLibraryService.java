package ee.svekko.booklibrary.service;

import ee.svekko.booklibrary.config.BookLibraryConfig;
import ee.svekko.booklibrary.dto.AddBookRequestDto;
import ee.svekko.booklibrary.dto.BookResponseDto;
import ee.svekko.booklibrary.error.InvalidDataError;
import ee.svekko.booklibrary.exception.InvalidDataException;
import ee.svekko.booklibrary.model.*;
import ee.svekko.booklibrary.repository.BookChangeRepository;
import ee.svekko.booklibrary.repository.BookRepository;
import ee.svekko.booklibrary.repository.BookStatusChangeRepository;
import ee.svekko.booklibrary.util.TimeUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    public List<BookResponseDto> getBooks(String title) {
        return bookChangeRepository.getBooks("%" + StringUtils.trim(title) + "%");
    }

    @Transactional
    public void addBook(UserAccount userAccount, AddBookRequestDto requestDto) {
        if (bookChangeRepository.getBookByTitle(requestDto.getTitle()).isPresent()) {
            throw new InvalidDataException(InvalidDataError.BOOK_MUST_HAVE_UNIQUE_TITLE);
        }

        Book book = bookRepository.save(Book.builder()
            .build());

        bookChangeRepository.save(BookChange.builder()
            .book(book)
            .title(requestDto.getTitle())
            .createdBy(userAccount)
            .validFrom(LocalDateTime.now())
            .validTo(TimeUtil.maxDateTime())
            .build());
    }

    @Transactional
    public void removeBook(UserAccount userAccount, int bookId) {
        BookChange bookChg = bookChangeRepository
            .getBookByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        if (!bookChg.getCreatedBy().getId().equals(userAccount.getId())) {
            throw new InvalidDataException(InvalidDataError.NOT_OWN_BOOK);
        }

        LocalDateTime now = LocalDateTime.now();

        bookChg.setValidTo(now);
        bookChangeRepository.save(bookChg);

        bookChangeRepository.save(BookChange.builder()
            .book(bookChg.getBook())
            .title(bookChg.getTitle())
            .createdBy(userAccount)
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
            throw new InvalidDataException(InvalidDataError.BOOK_NOT_AVAILABLE);
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
            throw new InvalidDataException(InvalidDataError.BOOK_NOT_AVAILABLE);
        }

        LocalDateTime validTo = LocalDateTime.now().plus(bookLibraryConfig.getBookDaysBorrowed(), ChronoUnit.DAYS);
        addBookStatus(bookId, BookStatusValue.BORROWED, userAccount, userAccount, validTo);
    }

    @Transactional
    public void completeBookReservation(UserAccount userAccount, int bookId) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        if (!BookStatusValue.RESERVED.equals(bookStatusChg.getBookStatus())) {
            throw new InvalidDataException(InvalidDataError.BOOK_NOT_RESERVED);
        }

        if (!userAccount.getId().equals(bookStatusChg.getBookUsedBy().getId()) && !isOwnBook(userAccount, bookId)) {
            throw new InvalidDataException(InvalidDataError.CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validTo = now.plus(bookLibraryConfig.getBookDaysBorrowed(), ChronoUnit.DAYS);

        bookStatusChg.setValidTo(now);
        bookStatusChangeRepository.save(bookStatusChg);

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(bookStatusChg.getBook())
            .bookStatus(BookStatus.builder().id(BookStatusValue.BORROWED.getId()).build())
            .bookUsedBy(bookStatusChg.getBookUsedBy())
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
            throw new InvalidDataException(InvalidDataError.BOOK_NOT_RESERVED);
        }

        if (!bookUsedBy.getId().equals(userAccount.getId()) && !isOwnBook(userAccount, bookId)) {
            throw new InvalidDataException(InvalidDataError.BOOK_RESERVED_FOR_SOMEONE_ELSE);
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
            throw new InvalidDataException(InvalidDataError.BOOK_NOT_BORROWED);
        }

        if (!bookUsedBy.getId().equals(userAccount.getId()) && !isOwnBook(userAccount, bookId)) {
            throw new InvalidDataException(InvalidDataError.BOOK_BORROWED_BY_SOMEONE_ELSE);
        }

        removeBookStatus(bookStatusChg);
    }

    private boolean isOwnBook(UserAccount userAccount, int bookId) {
        BookChange bookChg = bookChangeRepository
            .getBookByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        return bookChg.getCreatedBy().getId().equals(userAccount.getId());
    }

    private void addBookStatus(int bookId, BookStatusValue newStatus, UserAccount bookUsedBy, UserAccount changedBy, LocalDateTime validTo) {
        BookChange bookChg = bookChangeRepository
            .getBookByBookId(bookId)
            .orElseThrow(EntityNotFoundException::new);

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(bookChg.getBook())
            .bookStatus(BookStatus.builder().id(newStatus.getId()).build())
            .bookUsedBy(bookUsedBy)
            .validFrom(LocalDateTime.now())
            .validTo(validTo)
            .build());
    }

    private void removeBookStatus(BookStatusChange bookStatusChg) {
        bookStatusChg.setValidTo(LocalDateTime.now());
        bookStatusChangeRepository.save(bookStatusChg);
    }
}
