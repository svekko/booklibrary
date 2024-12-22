import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { environment } from "../../environments/environment";

@Injectable()
export class ApiService {
  http = inject(HttpClient);

  public apiAddr(endpoint: string) {
    return environment.apiURL + endpoint;
  }
}
