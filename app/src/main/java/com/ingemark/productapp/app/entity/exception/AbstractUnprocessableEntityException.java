package com.ingemark.productapp.app.entity.exception;

public abstract class AbstractUnprocessableEntityException extends RuntimeException
{
    protected AbstractUnprocessableEntityException(String message)
    {
        super(message);
    }
}

