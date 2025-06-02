package com.example.bankcards.exception.handlers;


import java.util.List;

/**
 * A response object representing validation errors in a request.
 * <p>
 * This record is used when handling {@code MethodArgumentNotValidException} and similar validation exceptions.
 * It encapsulates a list of {@link ErrorDetail} objects, each describing a specific field-level validation issue.
 * </p>
 *
 * <p><strong>Example JSON response:</strong></p>
 * <pre>{@code
 * {
 *   "violations": [
 *     { "field": "email", "message": "must be a valid email address" },
 *     { "field": "password", "message": "must not be blank" }
 *   ]
 * }
 * }</pre>
 *
 * @param violations list of field validation errors
 */
public record ValidationErrorResponse(List<ErrorDetail> violations) {

}