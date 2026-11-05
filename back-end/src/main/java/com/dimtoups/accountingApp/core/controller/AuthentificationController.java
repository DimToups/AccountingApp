package com.dimtoups.accountingApp.core.controller;

import com.dimtoups.accountingApp.core.controller.dto.authentification.*;
import com.dimtoups.accountingApp.core.helper.JwtHelper;
import com.dimtoups.accountingApp.core.mapper.user.UserControllerDtoToServiceDtoMapper;
import com.dimtoups.accountingApp.core.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthentificationController {

  private final AuthenticationManager authenticationManager;

  private final UserService userService;

  private final UserControllerDtoToServiceDtoMapper userControllerDtoToServiceDtoMapper;

  public AuthentificationController(AuthenticationManager authenticationManager, UserService userService, UserControllerDtoToServiceDtoMapper userControllerDtoToServiceDtoMapper) {
    this.authenticationManager = authenticationManager;
    this.userService = userService;
    this.userControllerDtoToServiceDtoMapper = userControllerDtoToServiceDtoMapper;
  }

  @PostMapping("/signup")
  public ResponseEntity<Void> signup(@Valid @RequestBody PostSignupRequestDto postSignupRequestDto) {
    try {
      userService.createUser(userControllerDtoToServiceDtoMapper.createUser(postSignupRequestDto));
    } catch (DuplicateKeyException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody PostLoginRequestDto loginRequestDto) {
    // Authenticating the user
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password()));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Generating the user's JWT
    String jwToken = JwtHelper.generateToken(loginRequestDto.username());

    return ResponseEntity.ok(new LoginResponseDto(jwToken));
  }

  @GetMapping("/csrfToken")
  public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken csrfToken) {
    return ResponseEntity.ok(csrfToken);
  }

}
