import { CommonModule } from "@angular/common";
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { catchError, of, tap } from "rxjs";
import { LoginService } from "./login.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private loginService = inject(LoginService);

  formGroup = new FormGroup({
    email: new FormControl("", {
      validators: [Validators.required, Validators.email, Validators.maxLength(256)],
      updateOn: "submit",
    }),
    password: new FormControl("", {
      validators: [Validators.required, Validators.maxLength(256)],
      updateOn: "submit",
    })
  });

  onSubmit() {
    this.formGroup.markAsTouched();

    if (this.formGroup.valid) {
      this.loginService
        .login(this.formGroup.getRawValue())
        .pipe(
          tap(() => window.location.pathname = "/books"),
          catchError(() => {
            this.formGroup.markAsUntouched();
            this.formGroup.patchValue({ "password": "" });
            return of(null);
          })
        )
        .subscribe();
    }
  }
}
