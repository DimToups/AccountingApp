package com.dimtoups.accountingApp.core.entity.authority;

import com.dimtoups.accountingApp.core.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * {@link Authority}'s composite key.
 * <br>
 * It contains the username and its authority.
 * @see Authority
 * @see User
 */
@Embeddable
public class AuthorityPk {

  /**
   * The referenced user's username.
   */
  @Column(name = "username")
  private String username;

  /**
   * The user's authority.
   * <br>
   * It must match one of {@link Authority.Authorities} values.
   * <p>
   * As a pertinent comment, even if the code for this field prevent any unwanted String from being used,
   * the information stored in the database might not match an {@code Authority.Authorities}
   * (this can happen if the stored value is modified by hand, for example).
   * Therefore, an authority should be threated as a string when gathered from an external source.
   * </p>
   */
  @Column
  private String authority;

  /**
   * An empty constructor used by Jakarta's {@link Embeddable} annotation.
   */
  public AuthorityPk() {

  }

  /**
   * Initializes a new {@link AuthorityPk} instance with their main information.
   * @param username The referenced user's username.
   * @param authority The wanted authority.
   */
  public AuthorityPk(String username, Authority.Authorities authority) {
    this.username = username;
    this.authority = authority.name();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(Authority.Authorities authority) {
    this.authority = authority.name();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof AuthorityPk)) {
      return false;
    }
    AuthorityPk primaryKey = (AuthorityPk) obj;

    if (!primaryKey.username.equals(this.username)) {
      return false;
    }

    if (!primaryKey.authority.equals(this.authority)) {
      return false;
    }

    return true;
  }
}
