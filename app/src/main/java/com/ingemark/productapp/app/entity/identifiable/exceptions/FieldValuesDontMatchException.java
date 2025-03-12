package com.ingemark.productapp.app.entity.identifiable.exceptions;

import static com.ingemark.productapp.app.entity.identifiable.EntityVerifier.createHashMap;

import java.util.Map;

import lombok.Getter;

import com.ingemark.productapp.app.entity.exception.AbstractUnprocessableEntityException;
import com.ingemark.productapp.app.entity.exception.ParameterizedException;

public class FieldValuesDontMatchException extends AbstractUnprocessableEntityException
    implements ParameterizedException
{
    @Getter
    private final Map<String, Object> exceptionParameters;

    public FieldValuesDontMatchException(String message, Integer entityId, Integer id)
    {
        super(message);
        this.exceptionParameters = createHashMap("entityId", entityId, "id", id);
    }
}
