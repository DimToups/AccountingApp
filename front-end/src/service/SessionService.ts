import { PostLoginRequest } from '../model/requests/PostLoginRequest';
import { LoginResponseDto } from '../model/responses/LoginResponseDto';
import { Observable, share } from 'rxjs';
import CsrfService from './CsrfService';
import { HttpClient } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { inject, Service } from '@angular/core';

@Service()
export class SessionService {
  public static readonly JWT_COOKIE_NAME: string = 'jwt';

  private httpClient: HttpClient;
  private cookieService: CookieService;

  constructor() {
    this.httpClient = inject(HttpClient);
    this.cookieService = inject(CookieService);
  }

  public login(postLoginRequest: PostLoginRequest): Observable<LoginResponseDto> {
    const observable = this.requestLogin(postLoginRequest);
    observable.subscribe({
      next: (response: LoginResponseDto): void => {
        // Handling JWT storage
        this.cookieService.set(SessionService.JWT_COOKIE_NAME, response.token, {
          expires: new Date(new Date().getTime() + 1000 * 60 * 5),
          secure: true,
          sameSite: 'Strict'
        });
      },
      error: () => {
        /* Nothing. We don't want to spam the console */
      }
    });

    return observable;
  }

  private requestLogin(postLoginRequest: PostLoginRequest): Observable<LoginResponseDto> {
    return this.httpClient
      .post<LoginResponseDto>(import.meta.env.VITE_SERVER_URL + '/login', postLoginRequest, {
        headers: {
          'X-XSRF-TOKEN': this.cookieService.get(CsrfService.TOKEN_NAME)
        },
        withCredentials: true
      })
      .pipe(share());
  }

  public logout(): void {
    this.cookieService.delete(SessionService.JWT_COOKIE_NAME);
  }

  public isSessionValid(): boolean {
    const jwt = this.cookieService.get(SessionService.JWT_COOKIE_NAME);

    if (jwt === '') {
      return false;
    }

    const match: RegExpMatchArray | null = jwt.match(
      '(^[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)'
    );

    return !(match === null || match.length == 0);
  }
}
