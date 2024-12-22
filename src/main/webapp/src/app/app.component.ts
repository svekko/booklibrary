import { CommonModule } from "@angular/common";
import { Component, OnInit, inject } from "@angular/core";
import { Router, RouterOutlet } from "@angular/router";
import { CookieService } from "ngx-cookie-service";
import { AlertComponent } from "./alert/alert.component";
import { AppService, UserInfo } from "./app.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet, CommonModule, AlertComponent],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.scss"
})

export class AppComponent implements OnInit {
  title = "webapp";
  userInfo: UserInfo | null = null;

  private appService = inject(AppService);
  private cookieService = inject(CookieService);
  private router = inject(Router);

  ngOnInit(): void {
    const userDataCookie = this.cookieService.get("user-data");

    if (userDataCookie) {
      const userData = JSON.parse(atob(userDataCookie));
      if (userData?.authenticated) {
        this.userInfo = { email: userData.email };
        this.router.navigateByUrl("/books");
        return;
      }
    }

    this.router.navigateByUrl("/login");
  }

  logout() {
    this.appService.logout().subscribe(() => {
      window.location.pathname = "/login";
    });
  }
}
