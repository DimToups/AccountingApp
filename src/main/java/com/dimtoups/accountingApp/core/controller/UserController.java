package com.dimtoups.accountingApp.core.controller;

import com.dimtoups.accountingApp.core.controller.dto.users.DeleteUserRequestDto;
import com.dimtoups.accountingApp.core.helper.JwtHelper;
import com.dimtoups.accountingApp.core.controller.dto.users.UpdateUserRequestDto;
import com.dimtoups.accountingApp.core.controller.dto.users.UserResponseDto;
import com.dimtoups.accountingApp.core.entity.user.User;
import com.dimtoups.accountingApp.core.mapper.user.UserControllerDtoToServiceDtoMapper;
import com.dimtoups.accountingApp.core.mapper.user.UserResponseDbMapper;
import com.dimtoups.accountingApp.core.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.FetchNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserResponseDbMapper userResponseMapper;

  @Autowired
  private UserControllerDtoToServiceDtoMapper userControllerDtoToServiceDtoMapper;

  @GetMapping("/user/{username}")
  public ResponseEntity<UserResponseDto> getUser(
      @PathVariable
      @Size(min = User.USERNAME_MIN_LENGTH, max = User.USERNAME_MAX_LENGTH) @Pattern(regexp = "(^[a-zA-Z0-9-_.~]*$)") @NotBlank
      String username,
      @RequestHeader(name = "Authorization") String bearerToken) {
    // Checking if the client can access the information
    if (!JwtHelper.isClientAuthorized(bearerToken, username)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    // Finding the user
    Optional<User> optionalUser = userService.findByUsername(username);
    if (optionalUser.isEmpty()) {
      // JwtAuthFilter should prevent from getting here by throwing a UsernameNotFoundException before even entering
      // this method
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    UserResponseDto response = userResponseMapper.dbUserToUserResponseDto(optionalUser.get());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/user")
  public ResponseEntity<String> deleteUser(
      @Valid @RequestBody DeleteUserRequestDto deleteUserDto,
      @RequestHeader(name = "Authorization") String bearerToken) {
    // Checking if the client can access the information
    if (!JwtHelper.isClientAuthorized(bearerToken, deleteUserDto.username())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    // Deleting the user
    try {
      userService.deleteUser(userControllerDtoToServiceDtoMapper.deleteUser(deleteUserDto));
    } catch (FetchNotFoundException e) {
      // JwtAuthFilter should prevent from getting here by throwing a UsernameNotFoundException before even entering
      // this method
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PatchMapping("/user")
  public ResponseEntity<Void> updateUser(
      @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto,
      @RequestHeader(name = "Authorization") String bearerToken) {
    // Checking if the client can access the information
    if (!JwtHelper.isClientAuthorized(bearerToken, updateUserRequestDto.username())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    // Update the user
    try {
      userService.updateUser(userControllerDtoToServiceDtoMapper.updateUser(updateUserRequestDto));
    } catch (FetchNotFoundException exception) {
      // JwtAuthFilter should prevent from getting here by throwing a UsernameNotFoundException before even entering
      // this method
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
