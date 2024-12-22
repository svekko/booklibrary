import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { ApiService } from "../api/api.service";

@Injectable({ providedIn: 'root' })
export class LoginService extends ApiService {
  login(body: any): Observable<any> {
    return this.http.post<any>(this.apiAddr("/login"), body, { withCredentials: true });
  }
}
