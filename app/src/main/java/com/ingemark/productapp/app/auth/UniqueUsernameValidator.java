package com.ingemark.productapp.app.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String>
{
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context)
    {
        if (username == null || username.isBlank())
        {
            return false;
        }
        return !userRepository.existsByUsername(username);
    }
}
