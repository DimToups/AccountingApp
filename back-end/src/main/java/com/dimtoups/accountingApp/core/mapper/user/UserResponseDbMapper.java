package com.dimtoups.accountingApp.core.mapper.user;

import com.dimtoups.accountingApp.core.controller.dto.users.UserResponseDto;
import com.dimtoups.accountingApp.core.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseDbMapper {

  UserResponseDto dbUserToUserResponseDto(User user);
}
