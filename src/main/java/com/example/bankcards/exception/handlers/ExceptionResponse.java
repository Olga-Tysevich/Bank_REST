package com.example.bankcards.exception.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A data transfer object (DTO) representing details of an exception response.
 * This class is used to provide structured error information in API responses.
 * <p>
 * It includes the HTTP status code, a main exception message, a list of detailed messages
 * (such as validation errors), and a timestamp indicating when the exception occurred.
 * </p>
 *
 * <p><strong>Example usage in a REST API:</strong></p>
 * <pre>{@code
 * {
 *   "httpStatusCode": 400,
 *   "exceptionMessage": "Validation failed",
 *   "exceptionDetails": ["Name must not be empty", "Email is not valid"],
 *   "timeStamp": "2025-06-02T10:15:30"
 * }
 * }</pre>
 *
 */
@Getter
@AllArgsConstructor
@Builder
public class ExceptionResponse {

    private final Integer HttpStatusCode;
    private final String ExceptionMessage;
    private final List<String> ExceptionDetails;
    private final LocalDateTime TimeStamp;

}