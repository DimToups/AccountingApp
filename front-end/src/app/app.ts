import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import CsrfService from '../service/CsrfService';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html'
})
export class App {
  protected readonly title = signal('AccountingApp-Front');

  constructor(csrfService: CsrfService) {
    csrfService.storeCsrfTokenIfNotPresent();
  }
}
