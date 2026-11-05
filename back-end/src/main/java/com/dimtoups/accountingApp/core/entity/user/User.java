package com.dimtoups.accountingApp.core.entity.user;

import jakarta.persistence.*;

/**
 * The user database entity.
 * <br>
 * It both contains common information about the user (like username, firstname, ...)
 * and information for their authentication (password, is their account enabled).
 */
@Entity(name="users")
public class User {

  public static final int USERNAME_MAX_LENGTH = 64;
  public static final int USERNAME_MIN_LENGTH = 1;

  public static final int FIRSTNAME_MAX_LENGTH = 64;
  public static final int FIRSTNAME_MIN_LENGTH = 1;

  public static final int LASTNAME_MAX_LENGTH = 64;
  public static final int LASTNAME_MIN_LENGTH = 1;

  // When talking about the password length, it references the entered value and not the encrypted value
  public static final int PASSWORD_MAX_LENGTH = 64;
  public static final int PASSWORD_MIN_LENGTH = 8;

  /**
   * The user's own username.
   * No other user can have the same username.
   */
  @Id
  @Column(nullable = false, unique = true)
  public String username;

  /**
   * The user's password used for authentication.
   * <br>
   * Passwords are stored in the BCrypt format, so the maximum length is 60 characters.
   */
  @Column(nullable = false)
  private String password;

  /**
   * A boolean indicating if the user account is enabled ({@code true}) or not ({@code false}).
   */
  @Column(nullable = false)
  private boolean enabled;

  /**
   * The user's firstname.
   */
  @Column
  private String firstname;

  /**
   * The user's lastname.
   */
  @Column
  private String lastname;

  /**
   * An empty constructor that can be used by Spring.
   */
  public User() {

  }

  /**
   * Creates an instance of {@link User} with a defined username.
   * @param username The wanted username
   */
  public User(String username) {
    this.username = username;
  }

  /**
   * Creates an instance of {@link User} with each of its non-nullable values for the database.
   * @param username The user's username
   * @param password The user's password
   * @param enabled A boolean indicating if the user is activated or not
   */
  public User(String username, String password, boolean enabled) {
    this.username = username;
    this.password = password;
    this.enabled = enabled;
  }

  public String getFirstname() {
    return this.firstname;
  }

  public String getLastname() {
    return this.lastname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
