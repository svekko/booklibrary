import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";
import { UserInfo } from "../model/user-info.dto";

@Injectable({ providedIn: 'root' })
export class UserService {
  private userInfo: BehaviorSubject<UserInfo> = new BehaviorSubject<UserInfo>({ authenticated: false });

  setUserInfo = (userInfo: UserInfo) => {
    this.userInfo.next(userInfo);
  };

  getUserInfo = () => {
    return this.userInfo.value;
  }
}
