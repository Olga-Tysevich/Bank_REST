package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ProhibitedException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Utility class for extracting information about the currently authenticated user from the security context.
 * Provides methods to retrieve the current user, their ID, and to perform authorization checks.
 *
 * <p>The class relies on Spring Security's {@link SecurityContextHolder} to get the authentication details
 * and extracts the user information if available. It ensures that the user is authenticated before extracting
 * user details or performing any checks.
 */
@Slf4j
@UtilityClass
public class PrincipalExtractor {

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * <p>If no authentication is found or the authentication is not authenticated, the method returns {@code null}.
     * The method checks if the principal object in the authentication is an instance of the {@link User} class.
     *
     * @return The current {@link User} object if the user is authenticated, or {@code null} if the user is not authenticated.
     */
    public static User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        return null;
    }

    /**
     * Retrieves the ID of the currently authenticated user from the security context.
     *
     * <p>Returns the user ID if the user is authenticated, or {@code null} if no authenticated user is found.
     *
     * @return The ID of the current user if available, or {@code null} if no authenticated user is found.
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();

        if (Objects.isNull(user)) {
            return null;
        }

        return user.getId();
    }

    /**
     * Checks if the current user is authenticated and throws an exception if the user is not found.
     *
     * <p>If the current user is {@code null} (i.e., not authenticated), a {@link ProhibitedException} is thrown.
     * This method is typically used to ensure that a user is logged in before performing actions that require authentication.
     *
     * @throws ProhibitedException If the user is not authenticated or is not present in the security context.
     */
    public static void checkCurrentUser() throws ProhibitedException {
        User user = getCurrentUser();
        if (Objects.isNull(user)) {
            log.error("Unauthorized access attempt. No current user found.");
            throw new ProhibitedException("Unauthorized access");
        }
    }

}