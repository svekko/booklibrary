import { HttpErrorResponse } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { NavigationEnd, Router } from "@angular/router";
import { BehaviorSubject } from "rxjs";

@Injectable({ providedIn: "root" })
export class AlertService {
  private error: BehaviorSubject<string> = new BehaviorSubject<string>("");
  private router = inject(Router);

  constructor() {
    this.router.events.subscribe((ev) => {
      if (ev instanceof NavigationEnd) {
        this.error.next("");
      }
    });
  }

  setError(err: any) {
    if (err instanceof HttpErrorResponse) {
      let msg = "Error";

      if (err.error?.error) {
        msg = `${msg}: ${err.error.error}`;
      }

      this.error.next(msg);
      return;
    }

    this.error.next(String(err));
  }

  getError() {
    return this.error.value;
  }
}
