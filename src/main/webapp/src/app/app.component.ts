import { CommonModule, Location } from "@angular/common";
import { Component, OnInit, inject } from "@angular/core";
import { Router, RouterOutlet } from "@angular/router";
import { CookieService } from "ngx-cookie-service";
import { AlertComponent } from "./alert/alert.component";
import { AppService } from "./app.service";
import { UserInfo } from "./model/user-info.dto";
import { UserService } from "./user/user.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet, CommonModule, AlertComponent],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.scss"
})

export class AppComponent implements OnInit {
  title = "webapp";

  private appService = inject(AppService);
  private cookieService = inject(CookieService);
  private router = inject(Router);
  private location = inject(Location);

  userService = inject(UserService);

  ngOnInit(): void {
    const userDataCookie = this.cookieService.get("user-data");

    if (userDataCookie) {
      let userData;

      try {
        userData = JSON.parse(atob(userDataCookie)) as UserInfo;
      } catch (e) {
        userData = { authenticated: false };
      }

      if (userData?.authenticated) {
        this.userService.setUserInfo(userData);
        this.router.navigateByUrl("/books");
        return;
      }
    }

    if (this.location.path() !== "/register") {
      this.router.navigateByUrl("/login");
    }
  }

  logout() {
    this.appService.logout().subscribe(() => {
      window.location.pathname = "/login";
    });
  }
}
