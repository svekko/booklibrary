import { HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({ providedIn: "root" })
export class AlertService {
  private error: string = "";

  setError(err: any) {
    if (err instanceof HttpErrorResponse) {
      let msg = "Error";

      if (err.error?.error) {
        msg = `${msg}: ${err.error.error}`;
      }

      this.error = msg;
      return;
    }

    this.error = String(err);
  }

  getError() {
    return this.error;
  }
}
