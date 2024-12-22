import { HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable({ providedIn: "root" })
export class AlertService {
  private error: BehaviorSubject<string> = new BehaviorSubject<string>("");

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
