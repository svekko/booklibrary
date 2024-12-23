import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { ApiService } from "../../api/api.service";
import { BookDto } from "../../model/book.dto";

@Injectable({ providedIn: 'root' })
export class BooksService extends ApiService {
  getBooks(title: string): Observable<any> {
    return this.http.get<BookDto[]>(this.apiAddr(`/books?title=${title}`), { withCredentials: true });
  }

  removeBook(bookId: number): Observable<void> {
    return this.http.delete<void>(this.apiAddr(`/books/${bookId}`), { withCredentials: true });
  }

  reserveBook(bookId: number): Observable<void> {
    return this.http.post<void>(this.apiAddr(`/books/${bookId}/reserve`), {}, { withCredentials: true });
  }

  borrowBook(bookId: number): Observable<void> {
    return this.http.post<void>(this.apiAddr(`/books/${bookId}/borrow`), {}, { withCredentials: true });
  }

  completeBookReservation(bookId: number): Observable<void> {
    return this.http.post<void>(this.apiAddr(`/books/${bookId}/complete-reservation`), {}, { withCredentials: true });
  }

  cancelBookReservation(bookId: number): Observable<void> {
    return this.http.post<void>(this.apiAddr(`/books/${bookId}/cancel-reservation`), {}, { withCredentials: true });
  }

  returnBook(bookId: number): Observable<void> {
    return this.http.post<void>(this.apiAddr(`/books/${bookId}/return`), {}, { withCredentials: true });
  }
}
