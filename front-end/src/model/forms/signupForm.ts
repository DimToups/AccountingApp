import { AutoMap } from '@automapper/classes';

export class SignupForm {
  @AutoMap(() => String)
  public username: string;

  @AutoMap(() => String)
  public password: string;

  public passwordCheck: string;

  constructor(username: string, password: string, passwordCheck: string) {
    this.username = username;
    this.password = password;
    this.passwordCheck = passwordCheck;
  }
}
