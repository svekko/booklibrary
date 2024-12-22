import { Routes } from '@angular/router';
import { BooksComponent } from "./pages/books/books.component";
import { LoginComponent } from "./pages/login/login.component";

export const routes: Routes = [
  { path: "books", component: BooksComponent },
  { path: "login", component: LoginComponent },
  { path: "**", redirectTo: "/login", pathMatch: "full" }
];
