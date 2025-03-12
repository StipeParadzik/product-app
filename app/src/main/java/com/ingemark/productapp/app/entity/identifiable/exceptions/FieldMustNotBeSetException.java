package com.ingemark.productapp.app.entity.identifiable.exceptions;

import java.util.Map;

import lombok.Getter;

import com.ingemark.productapp.app.entity.exception.AbstractUnprocessableEntityException;
import com.ingemark.productapp.app.entity.exception.ParameterizedException;

public class FieldMustNotBeSetException extends AbstractUnprocessableEntityException implements ParameterizedException
{
    @Getter
    private final Map<String, Object> exceptionParameters;

    public FieldMustNotBeSetException(String message, String fieldName)
    {
        super(message);
        this.exceptionParameters = Map.of("fieldName", fieldName);
    }
}

