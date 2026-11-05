import { createMap } from '@automapper/core';
import { mapper } from './mapper';
import { SignupForm } from '../model/forms/signupForm';
import { PostSignUpRequest } from '../model/requests/PostSignupRequest';

createMap(mapper, SignupForm, PostSignUpRequest);
