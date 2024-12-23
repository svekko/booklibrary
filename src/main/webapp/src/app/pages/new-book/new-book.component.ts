import { CommonModule } from "@angular/common";
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { catchError, of, tap } from "rxjs";
import { AlertService } from "../../alert/alert.service";
import { NewBookService } from "./new-book.service";

@Component({
  selector: 'app-new-book',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './new-book.component.html',
  styleUrl: './new-book.component.scss'
})
export class NewBookComponent {
  private newBookService = inject(NewBookService);
  private router = inject(Router);
  private alertService = inject(AlertService);

  formGroup = new FormGroup({
    title: new FormControl("", {
      validators: [Validators.required, Validators.maxLength(256)],
      updateOn: "submit",
    }),
  });

  toBooksPage = () => {
    this.router.navigateByUrl("/books");
  };

  onSubmit() {
    this.formGroup.markAsTouched();
    this.alertService.setError("");

    if (this.formGroup.valid) {
      this.newBookService
        .createNewBook(this.formGroup.getRawValue())
        .pipe(
          tap(() => this.toBooksPage()),
          catchError(() => {
            this.formGroup.markAsUntouched();
            this.formGroup.patchValue({ "title": "" });
            return of(null);
          })
        )
        .subscribe();
    }
  }
}
