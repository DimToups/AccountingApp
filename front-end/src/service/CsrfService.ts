import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { lastValueFrom, map, Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Service()
export default class CsrfService {
  private httpClient: HttpClient;
  private cookieService: CookieService;

  static TOKEN_NAME: string = 'XSRF-TOKEN';

  constructor() {
    this.httpClient = inject(HttpClient);
    this.cookieService = inject(CookieService);
  }

  public storeCsrfTokenIfNotPresent(): void {
    const token: string = this.cookieService.get(CsrfService.TOKEN_NAME);
    if (token === '' && !import.meta.env.SSR) {
      this.getCsrfToken().then((value: string) => {
        this.cookieService.set(CsrfService.TOKEN_NAME, value, {
          expires: 1,
          secure: true,
          sameSite: 'Strict'
        });
      });
    }
  }

  private async getCsrfToken(): Promise<string> {
    const request: Observable<string> = this.httpClient
      .get<CsrfToken>(import.meta.env.VITE_SERVER_URL + '/csrfToken', {
        mode: 'cors',
        withCredentials: true
      })
      .pipe(
        map((x: CsrfToken): string => {
          return x.token;
        })
      );

    return await lastValueFrom<string>(request);
  }
}
