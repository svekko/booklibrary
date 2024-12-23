import { inject } from "@angular/core";
import { Router, Routes, UrlTree } from '@angular/router';
import { Observable, of } from "rxjs";
import { BooksComponent } from "./pages/books/books.component";
import { LoginComponent } from "./pages/login/login.component";
import { NewBookComponent } from "./pages/new-book/new-book.component";
import { RegisterComponent } from "./pages/register/register.component";
import { UserService } from "./user/user.service";

const createCanActivateFn = (requireAuth: boolean) => {
  return (): Observable<boolean | UrlTree> => {
    const router = inject(Router);
    const userService = inject(UserService);

    if (userService.getUserInfo().authenticated && !requireAuth) {
      return of(router.parseUrl("/books"));
    }

    if (!userService.getUserInfo().authenticated && requireAuth) {
      return of(router.parseUrl("/login"));
    }

    return of(true);
  };
};

export const routes: Routes = [
  { path: "books", component: BooksComponent, canActivate: [createCanActivateFn(true)] },
  { path: "books/new", component: NewBookComponent, canActivate: [createCanActivateFn(true)] },
  { path: "login", component: LoginComponent, canActivate: [createCanActivateFn(false)] },
  { path: "register", component: RegisterComponent, canActivate: [createCanActivateFn(false)] },
  { path: "**", redirectTo: "/login", pathMatch: "full" },
];
