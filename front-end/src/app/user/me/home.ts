import { Component } from '@angular/core';
import { SessionService } from '../../../service/SessionService';
import { Router } from '@angular/router';
import { HomeDto } from '../../../model/pageDto/homeDto';

@Component({
  templateUrl: 'home.html'
})
export class Home {
  private readonly sessionService: SessionService;
  private readonly router: Router;
  protected homeDto: HomeDto;

  constructor(sessionService: SessionService, router: Router) {
    this.sessionService = sessionService;
    this.router = router;
    this.homeDto = new HomeDto('\"Ce n\'est peut-être pas mon vrai nom\"');

    this.checkSession();
  }

  protected logout(): void {
    this.sessionService.logout();
    this.router.navigateByUrl('/login');
  }

  private checkSession(): void {
    if (!this.sessionService.isSessionValid()) {
      this.router.navigateByUrl('/login');
    }
  }
}
