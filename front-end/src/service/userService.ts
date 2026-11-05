import { inject, Service } from '@angular/core';
import { Observable, share } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PostSignUpRequest } from '../model/requests/PostSignupRequest';
import CsrfService from './CsrfService';
import { CookieService } from 'ngx-cookie-service';

@Service()
export class UserService {
  private readonly httpClient: HttpClient;
  private readonly cookieService: CookieService;

  constructor() {
    this.httpClient = inject(HttpClient);
    this.cookieService = inject(CookieService);
  }

  public createUser(postSignupRequest: PostSignUpRequest): Observable<Object> {
    return this.requestSignup(postSignupRequest);
  }

  private requestSignup(postSignupRequest: PostSignUpRequest): Observable<Object> {
    return this.httpClient
      .post(import.meta.env.VITE_SERVER_URL + '/signup', postSignupRequest, {
        headers: {
          'X-XSRF-TOKEN': this.cookieService.get(CsrfService.TOKEN_NAME)
        },
        withCredentials: true
      })
      .pipe(share());
  }
}
