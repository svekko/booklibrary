import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { ApiService } from "./api/api.service";

@Injectable({ providedIn: 'root' })
export class AppService extends ApiService {
  logout(): Observable<any> {
    return this.http.post<any>(this.apiAddr("/logout"), {}, { withCredentials: true });
  }
}
