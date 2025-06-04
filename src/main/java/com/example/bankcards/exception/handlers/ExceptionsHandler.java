package com.example.bankcards.exception.handlers;

import com.example.bankcards.exception.InvalidRefreshTokenException;
import com.example.bankcards.exception.ProhibitedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.UnavailableException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * <p>
 * This class is annotated with {@code @RestControllerAdvice} and intercepts various exceptions
 * thrown during the processing of HTTP requests, providing meaningful and structured error responses.
 * </p>
 *
 * <p>It handles exceptions such as:</p>
 * <ul>
 *     <li>Authorization and access violations</li>
 *     <li>Malformed or incomplete requests</li>
 *     <li>Entity or resource not found</li>
 *     <li>Invalid authentication tokens or credentials</li>
 *     <li>Validation failures on method arguments</li>
 *     <li>Uncaught server-side exceptions</li>
 * </ul>
 *
 * <p>Each handler returns a consistent {@link ExceptionResponse} or {@link ValidationErrorResponse} object.</p>
 *
 * <p>Logged with SLF4J and returned with appropriate HTTP status codes.</p>
 */
@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler({
            AccessDeniedException.class,
            ProhibitedException.class
    })
    public ResponseEntity<?> accessExceptions(AccessDeniedException e) {
        return buildExceptionResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({
            HttpMediaTypeException.class,
            HttpMessageNotReadableException.class,
            MissingRequestCookieException.class,
            MissingRequestHeaderException.class,
            MissingRequestValueException.class
    })
    public ResponseEntity<?> badRequestExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            UsernameNotFoundException.class,
            InternalAuthenticationServiceException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<?> notFoundExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<?> conflictExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({
            UnavailableException.class,
            InvalidRefreshTokenException.class,
            AuthenticationCredentialsNotFoundException.class
    })
    public ResponseEntity<?> unauthorizedExceptions(Exception e) {
        return buildExceptionResponse(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationExceptions(MethodArgumentNotValidException e) {
        log.error("Validation Failed: {}", e.getMessage(), e);
        BindingResult result = e.getBindingResult();
        List<ErrorDetail> errorDetails = result.getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(errorDetails));
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<?> transactionSystemException(TransactionSystemException ex) {
        log.error("Transaction system exception: {}", ex.getMessage(), ex);

        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof ConstraintViolationException cve) {
            List<ErrorDetail> errors = cve.getConstraintViolations().stream()
                    .map(v -> new ErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(errors));
        }

        return internalServerError(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internalServerError(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage(), e);
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> noSuchElementException(NoSuchElementException e) {
        log.error("Resource not found: {}", e.getMessage(), e);

        String userMessage = "The requested resource was not found. Please check the ID and try again.";

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        HttpStatus.NOT_FOUND.value(),
                        userMessage,
                        List.of(e.getClass().getSimpleName()),
                        LocalDateTime.now()
                ));
    }

    /**
     * Builds a consistent {@link ExceptionResponse} object to be returned to the client.
     *
     * @param status the HTTP status to return
     * @param e      the exception that was thrown
     * @return a ResponseEntity containing the exception details
     */
    private ResponseEntity<Object> buildExceptionResponse(HttpStatus status, Exception e) {
        log.error("Exception occurred: {} - Status: {} - Message: {}",
                e.getClass().getSimpleName(), status, e.getMessage(), e);

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                status.value(),
                e.getMessage(),
                List.of(e.getClass().getSimpleName()),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(exceptionResponse);
    }
}