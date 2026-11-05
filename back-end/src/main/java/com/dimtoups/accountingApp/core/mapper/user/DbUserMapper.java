package com.dimtoups.accountingApp.core.mapper.user;

import com.dimtoups.accountingApp.core.entity.user.User;
import com.dimtoups.accountingApp.core.service.UserService;
import com.dimtoups.accountingApp.core.service.dto.users.UpdateUserDto;
import org.mapstruct.*;


@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DbUserMapper {
  /**
   * Updates an existing database user with a {@link UpdateUserDto}.
   * The update rules also applies on this method.
   * See {@link UserService#updateUser(UpdateUserDto)} for discovering these rules.
   * @param userTarget The database user to update
   * @param userSource The update dto containing every modified value and the username id.
   * @return The updated database user (a repository method call has to be done to make the update happen).
   * @see UserService#updateUser(UpdateUserDto)
   */
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "firstname", expression = "java(userSource.firstname() == null ? userTarget.getFirstname() : userSource.firstname().isEmpty() ? null : userSource.firstname())")
  @Mapping(target = "lastname", expression = "java(userSource.lastname() == null ? userTarget.getLastname() : userSource.lastname().isEmpty() ? null : userSource.lastname())")
  User updateUser(@MappingTarget User userTarget, UpdateUserDto userSource);
}
