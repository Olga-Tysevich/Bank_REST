package com.example.bankcards.exception;


import static com.example.bankcards.util.Constants.USER_NOT_FOUND;

/**
 * This exception is thrown when an attempt is made to perform an action that is not allowed for the given user.
 */
public class ProhibitedException extends RuntimeException {

    /**
     * Constructs a new ProhibitedException with a default error message.
     */
    public ProhibitedException() {
        super(USER_NOT_FOUND);
    }

    /**
     * Constructs a new ProhibitedException with a custom error message.
     *
     * @param message The custom error message to display.
     */
    public ProhibitedException(String message) {
        super(message);
    }


    /**
     * Constructs a new ProhibitedException with a custom error message and a cause.
     *
     * @param message The custom error message to display.
     * @param cause   The cause of the exception.
     */
    public ProhibitedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ProhibitedException with a cause.
     *
     * @param cause The cause of the exception.
     */
    public ProhibitedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ProhibitedException with a custom error message, cause, enable suppression and writable stack trace.
     *
     * @param message            The custom error message to display.
     * @param cause              The cause of the exception.
     * @param enableSuppression  Whether suppression is enabled or disabled.
     * @param writableStackTrace Whether the stack trace should be writable or not.
     */
    public ProhibitedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}