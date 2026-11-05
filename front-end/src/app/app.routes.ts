import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Root } from './root/root';
import { Home } from './user/me/home';
import { Signup } from './signup/signup';

export const routes: Routes = [
  {
    title: 'Accounting App',
    path: '',
    component: Root
  },
  {
    title: 'login',
    path: 'login',
    component: Login
  },
  {
    title: 'signup',
    path: 'signup',
    component: Signup
  },
  {
    title: 'Home',
    path: 'user/me',
    component: Home
  }
];
