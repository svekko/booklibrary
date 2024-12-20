package ee.smit.booklibrary.controller;

import ee.smit.booklibrary.dto.AddBookRequestDto;
import ee.smit.booklibrary.dto.BookResponseDto;
import ee.smit.booklibrary.dto.CompleteBookReservationRequestDto;
import ee.smit.booklibrary.model.UserAccount;
import ee.smit.booklibrary.service.BookLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookLibraryController {
    private final BookLibraryService bookLibraryService;

    @GetMapping("/books")
    public List<BookResponseDto> getBooks() {
        return bookLibraryService.getBooks();
    }

    @GetMapping("/own-books")
    public List<BookResponseDto> getOwnBooks() {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        return bookLibraryService.getOwnBooks(userAccount);
    }

    @PostMapping("/books")
    public void addBook(@RequestBody AddBookRequestDto requestDto) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.addBook(userAccount, requestDto);
    }

    @DeleteMapping("/books/{id}")
    public void removeBook(@PathVariable int id) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.removeBook(userAccount, id);
    }

    @PostMapping("/books/{id}/reserve")
    public void reserveBook(@PathVariable int id) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.reserveBook(userAccount, id);
    }

    @PostMapping("/books/{id}/borrow")
    public void borrowBook(@PathVariable int id) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.borrowBook(userAccount, id);
    }

    @PostMapping("/books/{id}/complete-reservation")
    public void completeBookReservation(@PathVariable int id, @RequestBody(required = false) CompleteBookReservationRequestDto requestDto) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.completeBookReservation(userAccount, requestDto, id);
    }

    @PostMapping("/books/{id}/cancel-reservation")
    public void cancelBookReservation(@PathVariable int id) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.cancelBookReservation(userAccount, id);
    }

    @PostMapping("/books/{id}/return")
    public void returnBook(@PathVariable int id) {
        UserAccount userAccount = UserAccount.fromSecurityContext();
        bookLibraryService.returnBook(userAccount, id);
    }
}
