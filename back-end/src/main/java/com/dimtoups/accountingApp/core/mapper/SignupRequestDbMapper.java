package com.dimtoups.accountingApp.core.mapper;

import com.dimtoups.accountingApp.core.entity.user.User;
import com.dimtoups.accountingApp.core.service.dto.users.CreateUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SignupRequestDbMapper {
  @Mapping(target = "enabled", ignore = true)
  User signupRequestToUser(CreateUserDto createUserDto);
}
