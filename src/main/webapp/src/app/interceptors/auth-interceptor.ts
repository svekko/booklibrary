import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, catchError, throwError } from "rxjs";
import { AlertService } from "../alert/alert.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private alertService = inject(AlertService);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next
      .handle(req)
      .pipe(catchError(err => this.handleError(err)));
  }

  private handleError(err: HttpErrorResponse): Observable<any> {
    let errMsg = "Error";

    if (err.error?.error) {
      errMsg = `${errMsg}: ${err.error.error}`;
    }

    this.alertService.setError(errMsg);
    return throwError(() => err);
  }
}
