import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { ApiService } from "../../api/api.service";

@Injectable({ providedIn: 'root' })
export class NewBookService extends ApiService {
  createNewBook(body: any): Observable<any> {
    return this.http.post<any>(this.apiAddr("/books"), body, { withCredentials: true });
  }
}
