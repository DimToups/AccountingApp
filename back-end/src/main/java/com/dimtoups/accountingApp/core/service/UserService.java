package com.dimtoups.accountingApp.core.service;

import com.dimtoups.accountingApp.core.entity.authority.Authority;
import com.dimtoups.accountingApp.core.entity.user.User;
import com.dimtoups.accountingApp.core.mapper.SignupRequestDbMapper;
import com.dimtoups.accountingApp.core.mapper.user.DbUserMapper;
import com.dimtoups.accountingApp.core.repository.AuthorizationRepository;
import com.dimtoups.accountingApp.core.repository.UserRepository;
import com.dimtoups.accountingApp.core.service.dto.users.CreateUserDto;
import com.dimtoups.accountingApp.core.service.dto.users.DeleteUserDto;
import com.dimtoups.accountingApp.core.service.dto.users.UpdateUserDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.FetchNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;


  //
  // Repositories
  //

  private final UserRepository userRepository;

  private final AuthorizationRepository authorizationRepository;


  //
  // Mappers
  //

  private final DbUserMapper userMapper;

  private final SignupRequestDbMapper signupRequestDbMapper;


  public UserService(UserRepository userRepository, DbUserMapper userMapper, PasswordEncoder passwordEncoder, SignupRequestDbMapper signupRequestDbMapper, AuthorizationRepository authorizationRepository) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.signupRequestDbMapper = signupRequestDbMapper;
    this.authorizationRepository = authorizationRepository;
  }

  /**
   * Updates a database user information of second importance (firstname, lastname).
   * <br>
   * Special rules applies to the update when giving a {@link UpdateUserDto}:
   * for keeping a value unchanged, a {@code null} value has to be given.
   * For setting a value to {@code null}, an empty string has to be given.
   * <br>
   * <br>
   * Exemple with a JSON object:
   * <br>
   * <pre>
   * {
   *   username: "John", // For updating the user "John"
   *   firstname: null,  // For letting its value unchanged in the database
   *   lastname: ""      // For setting its value to null in the database
   * }</pre>
   * @param updatedUser The DTO containing the user id (username), and its updated values
   * @return The updated database user
   * @throws FetchNotFoundException An exception thrown when the specified username does not match any user
   */
  public User updateUser(@Valid @NotNull UpdateUserDto updatedUser) throws FetchNotFoundException {
    // Checking if the database user exists
    Optional<User> optionalDbUser = userRepository.findByUsername(updatedUser.username());
    if (optionalDbUser.isEmpty()) {
      throw new FetchNotFoundException(User.class.getName(), updatedUser.username());
    }
    User dbUser = optionalDbUser.get();

    // Overriding non-null values from the updatedUser into the dbUser
    dbUser = userMapper.updateUser(dbUser, updatedUser);

    // Updating the user in the database
    return userRepository.save(dbUser);
  }

  @Transactional
  public void deleteUser(@Valid DeleteUserDto deleteUserDto) throws FetchNotFoundException {
    // Checking if the user exists
    Optional<User> optionalUser = userRepository.findByUsername(deleteUserDto.username());
    if (optionalUser.isEmpty()) {
      throw new FetchNotFoundException(User.class.getName(), deleteUserDto);
    }

    // Removing every constraint from the user
    authorizationRepository.deleteAllByUser(optionalUser.get());

    userRepository.deleteByUsername(deleteUserDto.username());
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public void createUser(@Valid CreateUserDto createUserDto) throws DuplicateKeyException {
    // Checking if the username does not already exist
    if (userRepository.findByUsername(createUserDto.username()).isPresent()) {
      throw new DuplicateKeyException("A user with the username " + createUserDto.username() + " already exists");
    }

    // Creating the database user
    User newUser = signupRequestDbMapper.signupRequestToUser(createUserDto);
    newUser.setPassword(passwordEncoder.encode(createUserDto.password()));
    newUser.setEnabled(true);
    userRepository.save(newUser);

    // Adding the default role to the user
    Authority authority = new Authority(newUser, Authority.Authorities.ROLE_USER);
    authorizationRepository.save(authority);
  }
}
