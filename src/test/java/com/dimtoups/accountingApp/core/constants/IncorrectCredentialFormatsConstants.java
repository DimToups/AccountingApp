package com.dimtoups.accountingApp.core.constants;

/**
 * A list of constants for impossible users, containing incorrect usernames and passwords.
 * Each "user" consists of a username and a password, both named with the same prefix.
 */
public class IncorrectCredentialFormatsConstants {
  public static final String SHORT_USERNAME = "P";
  public static final String SHORT_PASSWORD = "P";

  public static final String BLANK_USERNAME = " ";
  public static final String BLANK_PASSWORD = " ";

  public static final String EMPTY_USERNAME = "";
  public static final String EMPTY_PASSWORD = "Password";

  public static final String SPECIAL_CHARACTERS_USERNAME = "#-/\\\"";
  public static final String SPECIAL_CHARACTERS_PASSWORD = "#-/\\Password";

  public static final String LONG_USERNAME = "VeryLongUsernameWithMoreThanSixtyFourCharactersToTestMyApplication";
  public static final String LONG_PASSWORD = "VeryLongPasswordWithMoreThanSixtyFourCharactersToTestMyApplication";
}
