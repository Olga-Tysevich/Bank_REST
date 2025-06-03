package com.example.bankcards.exception;


import static com.example.bankcards.util.Constants.ACTION_IS_PROHIBITED;
import static com.example.bankcards.util.Constants.USER_NOT_FOUND;

/**
 * This exception is thrown when an attempt is made to retrieve a user that does not exist on the system.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with a default error message.
     */
    public UserNotFoundException(Long userId) {
        super(String.format(USER_NOT_FOUND, userId));
    }

    /**
     * Constructs a new UserNotFoundException with a custom error message.
     *
     * @param message The custom error message to display.
     */
    public UserNotFoundException(String message) {
        super(message);
    }


    /**
     * Constructs a new UserNotFoundException with a custom error message and a cause.
     *
     * @param message The custom error message to display.
     * @param cause   The cause of the exception.
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UserNotFoundException with a cause.
     *
     * @param cause The cause of the exception.
     */
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UserNotFoundException with a custom error message, cause, enable suppression and writable stack trace.
     *
     * @param message            The custom error message to display.
     * @param cause              The cause of the exception.
     * @param enableSuppression  Whether suppression is enabled or disabled.
     * @param writableStackTrace Whether the stack trace should be writable or not.
     */
    public UserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}