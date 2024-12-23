import { CommonModule } from "@angular/common";
import { Component, inject, OnInit } from '@angular/core';
import { Subscription, timer } from "rxjs";
import { BookStatus } from "../../model/book-status";
import { BookDto } from "../../model/book.dto";
import { dateTimeProvider } from "../../provider/date";
import { UserService } from "../../user/user.service";
import { BooksService } from "./books.service";

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [CommonModule],
  providers: [dateTimeProvider],
  templateUrl: './books.component.html',
  styleUrl: './books.component.scss'
})
export class BooksComponent implements OnInit {
  private booksService = inject(BooksService);
  private userService = inject(UserService);
  private searchTimer = Subscription.EMPTY;

  books: BookDto[] = [];

  protected BookStatus = BookStatus;

  borrowBook = (bookId: number) => {
    this.booksService.borrowBook(bookId).subscribe(() => this.loadBooks());
  };

  removeBook = (bookId: number) => {
    this.booksService.removeBook(bookId).subscribe(() => this.loadBooks());
  };

  reserveBook = (bookId: number) => {
    this.booksService.reserveBook(bookId).subscribe(() => this.loadBooks());
  };

  completeBookReservation = (bookId: number) => {
    this.booksService.completeBookReservation(bookId).subscribe(() => this.loadBooks());
  };

  cancelBookReservation = (bookId: number) => {
    this.booksService.cancelBookReservation(bookId).subscribe(() => this.loadBooks());
  };

  returnBook = (bookId: number) => {
    this.booksService.returnBook(bookId).subscribe(() => this.loadBooks());
  };

  searchBooks = (ev: Event) => {
    if (ev.currentTarget instanceof HTMLInputElement) {
      const value = ((ev.currentTarget) as HTMLInputElement).value;
      this.searchTimer.unsubscribe();
      this.searchTimer = timer(500).subscribe(() => this.loadBooks(value));
    }
  };

  loadBooks = (title = "") => {
    this.booksService.getBooks(title).subscribe((books) => {
      this.books = books;
    });
  };

  bookBelongsToMe = (book: BookDto) => {
    return book.bookUsedById === this.userService.getUserInfo().id;
  };

  bookIsCreatedByMe = (book: BookDto) => {
    return book.createdById === this.userService.getUserInfo().id;
  };

  ngOnInit(): void {
    this.loadBooks();
  }
}
