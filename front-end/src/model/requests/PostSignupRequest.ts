import { AutoMap } from '@automapper/classes';

export class PostSignUpRequest {
  @AutoMap(() => String)
  public username: string;

  @AutoMap(() => String)
  public password: string;

  constructor(username: string, password: string) {
    this.username = username;
    this.password = password;
  }
}
