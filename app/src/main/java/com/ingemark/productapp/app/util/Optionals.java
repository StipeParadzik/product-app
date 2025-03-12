package com.ingemark.productapp.app.util;

import java.util.Optional;

import lombok.experimental.UtilityClass;

import jakarta.validation.constraints.NotNull;

@UtilityClass
public class Optionals
{
    public static <T> T unwrapUserRequestedObject(@NotNull Optional<T> optional, Class<T> entityClass)
    {
        return optional.orElseThrow(() -> new ObjectNotFoundException(entityClass.getSimpleName() + " not found"));
    }
}
