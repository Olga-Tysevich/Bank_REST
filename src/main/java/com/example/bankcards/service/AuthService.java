package com.example.bankcards.service;

import com.example.bankcards.dto.api.req.UserLoginReqDTO;
import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The {@code AuthService} interface defines methods for authenticating and logging in users.
 * Implementations of this interface handle user login and token-based re-authentication (refresh token).
 */
public interface AuthService {

    /**
     * Authenticates a user and returns the logged-in user's details.
     * This method processes the login request, validates the credentials, and returns the user information along with a token.
     *
     * @param req The login request containing the user's credentials, encapsulated in a {@link UserLoginReqDTO} object.
     * @return A {@link LoggedUserRespDTO} containing the authenticated user's details and a token.
     * @throws IllegalArgumentException If the credentials are invalid or the login fails.
     */
    LoggedUserRespDTO loginUser(@NotNull UserLoginReqDTO req);

    /**
     * Re-authenticates the user using the provided refresh token.
     * This method is used to generate a new authentication token when the original token has expired.
     *
     * @param refreshToken The refresh token used to obtain a new authentication token.
     * @return A {@link LoggedUserRespDTO} containing the logged-in user's details and a new authentication token.
     * @throws IllegalArgumentException If the refresh token is invalid or has expired.
     */
    LoggedUserRespDTO reLoginUser(@NotBlank String refreshToken);
}
