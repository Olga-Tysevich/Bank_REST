package com.example.bankcards.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for handling HTTP errors and generating consistent error responses.
 * This class is designed to be extended by other classes that handle specific error cases (e.g., access denied).
 * It produces an HTTP response with a detailed problem description following the RFC 7807 standard for HTTP APIs.
 * <p>
 * The handler generates a structured response with an HTTP status, title, and detail message, and then writes it to
 * the HTTP response output stream.
 */
@Slf4j
public abstract class DefaultHandler {
    /**
     * Handles the error scenario by generating a {@link ProblemDetail} object with the relevant details (status,
     * title, and message) and writes this information to the HTTP response body in JSON format.
     * <p>
     * The HTTP status and response content type are set based on the specific subclass implementation.
     * This method logs the request URI and any error encountered during the process of writing the response.
     *
     * @param request the {@link HttpServletRequest} object that contains the incoming request
     * @param response the {@link HttpServletResponse} object used to send the response to the client
     * @throws IOException if an I/O error occurs while writing the response
     */
    public void handle(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        log.info("Start handling request: {}", request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus());
        problemDetail.setTitle(errorTitle());
        problemDetail.setDetail(errorMessage());

        log.debug("ProblemDetail created with status: {}, title: {}, detail: {}",
                problemDetail.getStatus(), problemDetail.getTitle(), problemDetail.getDetail());

        response.setStatus(httpServletResponse());
        response.setContentType(mediaType());

        try (OutputStream os = response.getOutputStream()) {
            new ObjectMapper().writeValue(os, problemDetail);
            os.flush();
        } catch (Exception e) {
            log.error("Error while writing response for request: {}", request.getRequestURI(), e);
        }
    }

    /**
     * Returns the {@link HttpStatus} to be set in the HTTP response. This should be implemented by the subclass
     * to specify the appropriate status code for the error.
     *
     * @return the {@link HttpStatus} representing the error status
     */
    protected abstract HttpStatus httpStatus();

    /**
     * Returns the error title to be used in the response body. This should be implemented by the subclass
     * to provide a relevant title for the specific error scenario.
     *
     * @return a string representing the error title (e.g., "Access Denied", "Unauthorized", etc.)
     */
    protected abstract String errorTitle();

    /**
     * Returns the error message to be used in the response body. This should be implemented by the subclass
     * to provide a detailed error message describing why the error occurred.
     *
     * @return a string representing the error message
     */
    protected abstract String errorMessage();

    /**
     * Returns the HTTP status code to be set in the response. This should be implemented by the subclass
     * to specify the appropriate HTTP status code for the error (e.g., 403 for Forbidden).
     *
     * @return an integer representing the HTTP status code (e.g., 403)
     */
    protected abstract int httpServletResponse();

    /**
     * Returns the media type to be set in the response. This should be implemented by the subclass
     * to specify the correct media type for the response (e.g., "application/problem+json").
     *
     * @return a string representing the media type for the response
     */
    protected abstract String mediaType();
}