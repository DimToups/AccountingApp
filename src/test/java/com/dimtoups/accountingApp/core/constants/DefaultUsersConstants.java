package com.dimtoups.accountingApp.core.constants;

import com.dimtoups.accountingApp.core.entity.user.User;

public class DefaultUsersConstants {
  /**
   * A generic username present in the CreateDefaultUser.sql script.
   * A {@link User} should be found in the test database with this username.
   */
  public static final String DEFAULT_USERNAME = "Pizza";

  /**
   * The unhashed password used for the {@link DefaultUsersConstants#DEFAULT_USERNAME default user}.
   * This constant has to be used for logging in the default user.
   * <p>
   * The hashed password is {@code $2a$10$4i9lo2GJmA.45ZMCuHYgIO4e6Zzla6KllILrRL0wiAHYNEZG8OJyS}.
   * </p>
   */
  public static final String DEFAULT_PASSWORD = "git_add_.env.production";
}
