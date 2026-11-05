package com.dimtoups.accountingApp.core.repository;

import com.dimtoups.accountingApp.core.entity.user.User;
import com.dimtoups.accountingApp.core.entity.authority.AuthorityPk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, AuthorityPk> {
  @Autowired
  Optional<User> findByUsername(String username);

  @Autowired
  Optional<User> deleteByUsername(String username);
}
