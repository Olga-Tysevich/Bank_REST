package com.example.bankcards.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static com.example.bankcards.util.Constants.ACCESS_DENIED;
import static com.example.bankcards.util.Constants.ACCESS_DENIED_MESSAGE;

/**
 * A custom implementation of {@link AccessDeniedHandler} to handle cases where access to a resource is denied.
 * This handler will return a JSON response containing the error details when a user does not have permission to
 * access a specific resource. The response will have an HTTP status of {@link HttpStatus#FORBIDDEN} (403).
 * <p>
 * It extends from the {@link DefaultHandler} to provide a consistent response format for access denied errors.
 */
public class ApiAccessDeniedHandler extends DefaultHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        super.handle(request, response);
    }

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    protected String errorTitle() {
        return ACCESS_DENIED;
    }

    @Override
    protected String errorMessage() {
        return ACCESS_DENIED_MESSAGE;
    }

    @Override
    protected int httpServletResponse() {
        return HttpServletResponse.SC_FORBIDDEN;
    }

    @Override
    protected String mediaType() {
        return MediaType.APPLICATION_PROBLEM_JSON_VALUE;
    }
}
