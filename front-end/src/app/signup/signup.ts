import { Component, signal, WritableSignal } from '@angular/core';
import { FieldTree, form, FormField, FormRoot, required } from '@angular/forms/signals';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { UserService } from '../../service/userService';
import { SignupForm } from '../../model/forms/signupForm';
import { PostSignUpRequest } from '../../model/requests/PostSignupRequest';
import 'reflect-metadata';
import { mapper } from '../../mappings/mapper';
import '../../mappings/register';

@Component({
  imports: [FormRoot, FormField],
  templateUrl: 'signup.html'
})
export class Signup {
  private router: Router;
  private userService: UserService;

  constructor(router: Router, userService: UserService) {
    this.router = router;
    this.userService = userService;
  }

  protected signupModel: WritableSignal<SignupForm> = signal(new SignupForm('', '', ''));

  protected signupForm: FieldTree<SignupForm> = form(
    this.signupModel,
    (schemaPath) => {
      required(schemaPath.username);
      required(schemaPath.password);
      required(schemaPath.passwordCheck);
    },
    {
      submission: {
        action: async (field) => {
          const formValues: SignupForm = field().value();
          if (formValues.password !== formValues.passwordCheck) {
            this.focusPasswordCheck();
            return;
          }

          this.userService
            .createUser(mapper.map(field().value(), SignupForm, PostSignUpRequest))
            .subscribe({
              next: (_) => {
                this.router.navigateByUrl('/login');
              },
              error: (_: HttpErrorResponse) => {
                this.signupModel.set(new SignupForm('', '', ''));
              }
            });
        }
      }
    }
  );

  private focusPasswordCheck(): void {
    this.signupForm.passwordCheck().focusBoundControl();
  }
}
