import { Component, signal } from '@angular/core';
import { PostLoginRequest } from '../../model/requests/PostLoginRequest';
import { form, FormField, FormRoot, required } from '@angular/forms/signals';
import { SessionService } from '../../service/SessionService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  templateUrl: 'login.html',
  imports: [FormField, FormRoot]
})
export class Login {
  private sessionService: SessionService;
  private router: Router;

  constructor(sessionService: SessionService, router: Router) {
    this.sessionService = sessionService;
    this.router = router;
  }

  protected loginModel = signal(new PostLoginRequest('', ''));

  protected loginForm = form(
    this.loginModel,
    (schemaPath) => {
      required(schemaPath.username);
      required(schemaPath.password);
    },
    {
      submission: {
        action: async (field) => {
          this.sessionService.login(field().value()).subscribe({
            next: (_) => {
              this.router.navigateByUrl('/user/me');
            },
            error: (_: HttpErrorResponse) => {
              this.loginModel.set(new PostLoginRequest('', ''));
            }
          });
        }
      }
    }
  );
}
