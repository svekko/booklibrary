import { CommonModule } from "@angular/common";
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { catchError, of, tap } from "rxjs";
import { RegisterService } from "./register.service";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private registerService = inject(RegisterService);
  private router = inject(Router);

  formGroup = new FormGroup({
    email: new FormControl("", {
      validators: [Validators.required, Validators.email, Validators.maxLength(256)],
      updateOn: "submit",
    }),
    password: new FormControl("", {
      validators: [Validators.required, Validators.maxLength(256)],
      updateOn: "submit",
    }),
    passwordConfirm: new FormControl("", {
      validators: [Validators.required, Validators.maxLength(256)],
      updateOn: "submit",
    }),
  });

  toLoginPage = () => {
    this.router.navigateByUrl("/login");
  };

  onSubmit() {
    this.formGroup.markAsTouched();

    if (this.formGroup.valid) {
      this.registerService
        .register(this.formGroup.getRawValue())
        .pipe(
          tap(() => window.location.pathname = "/login"),
          catchError(() => {
            this.formGroup.markAsUntouched();
            this.formGroup.patchValue({ "password": "", "passwordConfirm": "" });
            return of(null);
          })
        )
        .subscribe();
    }
  }
}
