import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { CookieService } from "ngx-cookie-service";
import { EMPTY, Observable, catchError, throwError } from "rxjs";
import { AlertService } from "../alert/alert.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private alertService = inject(AlertService);
  private cookieService = inject(CookieService);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.method !== "GET") {
      this.alertService.setError("");
    }

    return next
      .handle(req)
      .pipe(catchError(err => this.handleError(err)));
  }

  private handleError(err: HttpErrorResponse): Observable<any> {
    if (err.status === 403) {
      this.cookieService.delete("user-data");
      window.location.pathname = "/login";
      return EMPTY;
    }

    let errMsg = "Error";

    if (err.error?.error) {
      errMsg = `${errMsg}: ${err.error.error}`;
    }

    this.alertService.setError(errMsg);
    return throwError(() => err);
  }
}
