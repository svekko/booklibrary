import { CommonModule } from "@angular/common";
import { Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from "@angular/router";
import { finalize, Subscription, timer } from "rxjs";
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
  private router = inject(Router);
  private searchTimer = Subscription.EMPTY;

  loading = true;
  books: BookDto[] = [];

  protected BookStatus = BookStatus;

  @ViewChild("searchInput")
  private searchInput!: ElementRef;

  borrowBook = (bookId: number) => {
    this.booksService.borrowBook(bookId).subscribe(() => this.resetBooks());
  };

  removeBook = (bookId: number) => {
    this.booksService.removeBook(bookId).subscribe(() => this.resetBooks());
  };

  reserveBook = (bookId: number) => {
    this.booksService.reserveBook(bookId).subscribe(() => this.resetBooks());
  };

  completeBookReservation = (bookId: number) => {
    this.booksService.completeBookReservation(bookId).subscribe(() => this.resetBooks());
  };

  cancelBookReservation = (bookId: number) => {
    this.booksService.cancelBookReservation(bookId).subscribe(() => this.resetBooks());
  };

  returnBook = (bookId: number) => {
    this.booksService.returnBook(bookId).subscribe(() => this.resetBooks());
  };

  searchBooks = (ev: Event) => {
    if (ev.currentTarget instanceof HTMLInputElement) {
      const value = ((ev.currentTarget) as HTMLInputElement).value;
      this.loading = true;
      this.searchTimer.unsubscribe();
      this.searchTimer = timer(500).subscribe(() => this.loadBooks(value));
    }
  };

  private resetBooks = () => {
    this.searchInput.nativeElement.value = "";
    this.loadBooks();
  };

  loadBooks = (title = "") => {
    this.booksService.getBooks(title)
      .pipe(finalize(() => {
        this.loading = false;
      }))
      .subscribe((books) => {
        this.books = books;
      });
  };

  bookBelongsToMe = (book: BookDto) => {
    return book.bookUsedById === this.userService.getUserInfo().id;
  };

  bookIsCreatedByMe = (book: BookDto) => {
    return book.createdById === this.userService.getUserInfo().id;
  };

  goToNewBookPage = () => {
    this.router.navigateByUrl("/books/new");
  };

  ngOnInit(): void {
    this.loadBooks();
  }
}
