package ee.smit.booklibrary.service;

import ee.smit.booklibrary.dto.AddBookRequestDto;
import ee.smit.booklibrary.dto.BookResponseDto;
import ee.smit.booklibrary.dto.CompleteBookReservationRequestDto;
import ee.smit.booklibrary.exception.BookActionException;
import ee.smit.booklibrary.model.*;
import ee.smit.booklibrary.repository.BookRepository;
import ee.smit.booklibrary.repository.BookStatusChangeRepository;
import ee.smit.booklibrary.util.TimeUtil;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLibraryService {
    private final BookStatusChangeRepository bookStatusChangeRepository;
    private final BookRepository bookRepository;

    public List<BookResponseDto> getBooks() {
        return bookRepository.getBooks()
            .stream()
            .map(Book::toBookResponseDto)
            .toList();
    }

    public List<BookResponseDto> getOwnBooks(UserAccount userAccount) {
        return bookRepository.getBooksWhereAddedBy(userAccount.getId())
            .stream()
            .map(Book::toBookResponseDto)
            .toList();
    }

    @Transactional
    public void addBook(UserAccount userAccount, AddBookRequestDto requestDto) {
        Book book = bookRepository.save(Book.builder()
            .title(requestDto.getTitle())
            .addedBy(userAccount)
            .build());

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(book)
            .bookStatus(BookStatus.builder().id(BookStatusValue.NOT_IN_USE.getId()).build())
            .changedBy(userAccount)
            .validFrom(LocalDateTime.now())
            .validTo(TimeUtil.maxDateTime())
            .build());
    }

    @Transactional
    public void removeBook(UserAccount userAccount, int id) {
        if (!isOwnBook(userAccount, id)) {
            throw new BookActionException(BookActionException.Error.NOT_OWN_BOOK);
        }

        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(id)
            .orElseThrow(EntityNotFoundException::new);

        bookStatusChg.setValidTo(LocalDateTime.now());
        bookStatusChangeRepository.save(bookStatusChg);

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(bookStatusChg.getBook())
            .bookStatus(BookStatus.builder().id(BookStatusValue.NOT_IN_USE.getId()).build())
            .changedBy(userAccount)
            .validFrom(bookStatusChg.getValidTo())
            .validTo(bookStatusChg.getValidTo())
            .build());
    }

    @Transactional
    public void reserveBook(UserAccount userAccount, int id) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(id)
            .orElseThrow(EntityNotFoundException::new);

        if (!BookStatusValue.NOT_IN_USE.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_AVAILABLE);
        }

        editBookStatus(bookStatusChg, BookStatusValue.RESERVED, userAccount, userAccount);
    }

    @Transactional
    public void borrowBook(UserAccount userAccount, int id) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(id)
            .orElseThrow(EntityNotFoundException::new);

        if (!BookStatusValue.NOT_IN_USE.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_AVAILABLE);
        }

        editBookStatus(bookStatusChg, BookStatusValue.BORROWED, userAccount, userAccount);
    }

    @Transactional
    public void completeBookReservation(UserAccount userAccount, @Nullable CompleteBookReservationRequestDto requestDto, int id) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(id)
            .orElseThrow(EntityNotFoundException::new);

        UserAccount reservedTo = UserAccount.builder()
            .id(requestDto != null ? requestDto.getReservedToAccountId() : userAccount.getId())
            .build();

        if (!BookStatusValue.RESERVED.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_RESERVED);
        }

        if (!userAccount.getId().equals(reservedTo.getId()) && !isOwnBook(userAccount, id)) {
            throw new BookActionException(BookActionException.Error.CAN_COMPLETE_ONLY_OWN_BOOK_RESERVATION);
        }

        editBookStatus(bookStatusChg, BookStatusValue.BORROWED, reservedTo, userAccount);
    }

    @Transactional
    public void cancelBookReservation(UserAccount userAccount, int id) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(id)
            .orElseThrow(EntityNotFoundException::new);

        UserAccount bookUsedBy = bookStatusChg.getBookUsedBy();

        if (!BookStatusValue.RESERVED.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_RESERVED);
        }

        if (!bookUsedBy.getId().equals(userAccount.getId()) && !isOwnBook(userAccount, id)) {
            throw new BookActionException(BookActionException.Error.BOOK_RESERVED_FOR_SOMEONE_ELSE);
        }

        editBookStatus(bookStatusChg, BookStatusValue.NOT_IN_USE, null, userAccount);
    }

    @Transactional
    public void returnBook(UserAccount userAccount, int id) {
        BookStatusChange bookStatusChg = bookStatusChangeRepository
            .findValidBookStatusChangeByBookId(id)
            .orElseThrow(EntityNotFoundException::new);

        UserAccount bookUsedBy = bookStatusChg.getBookUsedBy();

        if (!BookStatusValue.BORROWED.equals(bookStatusChg.getBookStatus())) {
            throw new BookActionException(BookActionException.Error.BOOK_NOT_BORROWED);
        }

        if (!bookUsedBy.getId().equals(userAccount.getId()) && !isOwnBook(userAccount, id)) {
            throw new BookActionException(BookActionException.Error.BOOK_BORROWED_BY_SOMEONE_ELSE);
        }

        editBookStatus(bookStatusChg, BookStatusValue.NOT_IN_USE, null, userAccount);
    }

    private boolean isOwnBook(UserAccount userAccount, int bookId) {
        Book book = bookRepository.findValidBookByBookId(bookId).orElseThrow(EntityNotFoundException::new);
        return book.getAddedBy().getId().equals(userAccount.getId());
    }

    private void editBookStatus(BookStatusChange bookStatusChg, BookStatusValue newStatus, UserAccount bookUsedBy, UserAccount changedBy) {
        bookStatusChg.setValidTo(LocalDateTime.now());
        bookStatusChangeRepository.save(bookStatusChg);

        bookStatusChangeRepository.save(BookStatusChange.builder()
            .book(bookStatusChg.getBook())
            .bookStatus(BookStatus.builder().id(newStatus.getId()).build())
            .bookUsedBy(bookUsedBy)
            .changedBy(changedBy)
            .validFrom(bookStatusChg.getValidTo())
            .validTo(TimeUtil.maxDateTime())
            .build());
    }
}
