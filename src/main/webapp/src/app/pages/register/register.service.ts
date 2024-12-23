import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { ApiService } from "../../api/api.service";

@Injectable({ providedIn: 'root' })
export class RegisterService extends ApiService {
  register(body: any): Observable<any> {
    return this.http.post<any>(this.apiAddr("/register"), body, { withCredentials: true });
  }
}
