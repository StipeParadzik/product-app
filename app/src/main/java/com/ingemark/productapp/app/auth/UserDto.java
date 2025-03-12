package com.ingemark.productapp.app.auth;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserDto
{
    @NotBlank
    @UniqueUsername
    private String username;

    @NotBlank
    private String password;

    private Role role;
}
