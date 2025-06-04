package com.example.bankcards.exception;


import static com.example.bankcards.util.Constants.INVALID_REFRESH_TOKEN;

/**
 * Exception is thrown when a refresh token is invalid.
 */
public class InvalidRefreshTokenException extends RuntimeException {

    /**
     * Constructs a new InvalidRefreshTokenException with a default message.
     */
    public InvalidRefreshTokenException() {
        super(INVALID_REFRESH_TOKEN);
    }

    /**
     * Constructs a new InvalidRefreshTokenException with the specified message.
     *
     * @param message The detail message.
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidRefreshTokenException with the specified message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public InvalidRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidRefreshTokenException with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public InvalidRefreshTokenException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new InvalidRefreshTokenException with the specified message, cause, enableSuppression flag, and writableStackTrace flag.
     *
     * @param message            The detail message.
     * @param cause              The cause of the exception.
     * @param enableSuppression  Whether suppression is enabled or disabled.
     * @param writableStackTrace Whether the stack trace should be writable.
     */
    public InvalidRefreshTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}