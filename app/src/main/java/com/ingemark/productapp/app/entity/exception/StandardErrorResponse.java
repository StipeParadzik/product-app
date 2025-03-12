package com.ingemark.productapp.app.entity.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record StandardErrorResponse(int status, String error, String message, LocalDateTime timestamp,
                                    Map<String, Object> details)
{
}

