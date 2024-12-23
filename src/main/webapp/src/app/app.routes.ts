import { Routes } from '@angular/router';
import { BooksComponent } from "./pages/books/books.component";
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";

export const routes: Routes = [
  { path: "books", component: BooksComponent },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "**", redirectTo: "/login", pathMatch: "full" }
];
