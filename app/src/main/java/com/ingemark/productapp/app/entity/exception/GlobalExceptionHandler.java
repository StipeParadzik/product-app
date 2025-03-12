package com.ingemark.productapp.app.entity.exception;

import static com.ingemark.productapp.app.entity.identifiable.EntityVerifier.createHashMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request)
    {
        BindingResult result = ex.getBindingResult();
        SetMultimap<String, Map<String, Object>> errors = HashMultimap.create();

        for (FieldError fieldError : result.getFieldErrors())
        {
            errors.put(fieldError.getField(),
                createHashMap("errorCode", fieldError.getCode(), "message", fieldError.getDefaultMessage()));
        }

        if (!result.getGlobalErrors()
            .isEmpty())
        {
            List<Map<String, Object>> globalErrorList = new ArrayList<>();
            for (ObjectError globalError : result.getGlobalErrors())
            {
                globalErrorList.add(createHashMap("errorCode",
                    globalError.getCode(),
                    "message",
                    globalError.getDefaultMessage()));
            }
            errors.put("global", Map.of("errors", globalErrorList));
        }

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        responseBody.put("error", "Validation failed");
        responseBody.put("fieldErrors", errors.asMap());
        responseBody.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseBody, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex)
    {
        SetMultimap<String, Map<String, Object>> errors = HashMultimap.create();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations())
        {
            errors.put(violation.getPropertyPath()
                    .toString(),
                Map.of("errorCode",
                    violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .getSimpleName(),
                    "message",
                    violation.getMessage()));
        }

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", "Validation failed");
        responseBody.put("fieldErrors", errors.asMap());
        responseBody.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ IllegalArgumentException.class, EntityNotFoundException.class })
    public ResponseEntity<Object> handleRuntimeExceptions(RuntimeException ex)
    {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", "Bad Request");
        responseBody.put("message", ex.getMessage());
        responseBody.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalExceptions(Exception ex)
    {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseBody.put("error", "Internal Server Error");
        responseBody.put("message", ex.getMessage());
        responseBody.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AbstractUnprocessableEntityException.class)
    public ResponseEntity<StandardErrorResponse> handleAbstractUnprocessableEntityException(RuntimeException ex)
    {
        return buildResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<StandardErrorResponse> buildResponse(RuntimeException ex, HttpStatus status)
    {
        return ResponseEntity.status(status)
            .body(new StandardErrorResponse(status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now(),
                (ex instanceof ParameterizedException parametrizedException)
                    ? parametrizedException.getExceptionParameters()
                    : Map.of()));
    }
}


