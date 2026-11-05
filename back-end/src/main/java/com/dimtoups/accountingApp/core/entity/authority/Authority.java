package com.dimtoups.accountingApp.core.entity.authority;

import com.dimtoups.accountingApp.core.entity.user.User;
import jakarta.persistence.*;

/**
 * The users authorities entity.
 * <br>
 * It simply contains the username and their authorities.
 * The entity has a composite key on both the username and the authority to prevent duplicates.
 * The composite key is stored in the {@link AuthorityPk} class.
 */
@Entity(name = "authorities")
public class Authority {

  /**
   * The entity's composite key.
   */
  @EmbeddedId
  private AuthorityPk authorityPk;

  /**
   * The foreign key to the targeted user.
   * <br>
   * The referenced column is stored in this instance's {@link AuthorityPk#getUsername() AuthorityPk#username}.
   */
  @ManyToOne
  @MapsId(value = "username")
  @JoinColumn(name = "username", referencedColumnName = "username")
  private User user;


  /**
   * An empty constructor used by Spring and its extensions.
   */
  public Authority() {

  }

  /**
   * Initializes a new {@link Authority} instance.
   * @param user The user to give an authority.
   * @param authority The user's authority.
   */
  public Authority(User user, Authorities authority) {
    this.authorityPk = new AuthorityPk(user.getUsername(), authority);
    this.user = user;
  }


  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public AuthorityPk getAuthorityPk() {
    return authorityPk;
  }

  public void setAuthorityPk(AuthorityPk authorityPk) {
    this.authorityPk = authorityPk;
  }

  /**
   * The list of possible authorities in the application.
   */
  public enum Authorities {
    ROLE_ADMIN,
    ROLE_USER
  }
}
