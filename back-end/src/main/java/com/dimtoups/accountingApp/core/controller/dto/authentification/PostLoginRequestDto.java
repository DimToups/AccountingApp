package com.dimtoups.accountingApp.core.controller.dto.authentification;

import com.dimtoups.accountingApp.core.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PostLoginRequestDto(
    @Size(min = User.LASTNAME_MIN_LENGTH, max = User.LASTNAME_MAX_LENGTH)
    @Pattern(regexp = "(^[a-zA-Z0-9-_.~]*$)")
    @NotBlank
    String username,

    @Size(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
    @NotBlank
    String password) {
}
