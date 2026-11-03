package com.dimtoups.accountingApp.core.controller.dto.users;

import com.dimtoups.accountingApp.core.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(
    @Size(min = User.USERNAME_MIN_LENGTH, max = User.USERNAME_MAX_LENGTH)
    @Pattern(regexp = "(^[a-zA-Z0-9-_.~]*$)")
    @NotBlank
    String username,

    @Size(max = User.FIRSTNAME_MAX_LENGTH)
    String firstname,

    @Size(max = User.LASTNAME_MAX_LENGTH)
    String lastname) {

}
