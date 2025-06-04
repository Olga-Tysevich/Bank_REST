package com.example.bankcards.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static com.example.bankcards.util.Constants.NOT_AUTHORIZED;
import static com.example.bankcards.util.Constants.UNKNOWN_USER;

/**
 * Custom implementation of {@link AuthenticationEntryPoint} to handle unauthorized access attempts.
 * This class is invoked when an authentication exception occurs (e.g., when a user is not authenticated).
 * It generates a consistent error response with a 401 Unauthorized status, including a detailed error message in JSON format.
 * <p>
 * The handler extends {@link DefaultHandler} and provides a structured response that includes an HTTP status,
 * title, and message, consistent with the Problem Details for HTTP APIs (RFC 7807).
 */
public class ForbiddenEntryPoint extends DefaultHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        super.handle(request, response);
    }

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    protected String errorTitle() {
        return NOT_AUTHORIZED;
    }

    @Override
    protected String errorMessage() {
        return UNKNOWN_USER;
    }

    @Override
    protected int httpServletResponse() {
        return HttpServletResponse.SC_UNAUTHORIZED;
    }

    @Override
    protected String mediaType() {
        return MediaType.APPLICATION_PROBLEM_JSON_VALUE;
    }
}