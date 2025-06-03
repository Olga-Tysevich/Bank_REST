package com.example.bankcards.service;

import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import com.example.bankcards.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The {@code JwtService} interface defines methods responsible for generating and regenerating JWT (JSON Web Token) tokens.
 * These methods are used for user authentication by issuing access and refresh tokens.
 */
public interface JwtService {

    /**
     * Generates a pair of JWT tokens (access and refresh tokens) for the specified user.
     * This method is typically used when the user logs in or is authenticated for the first time.
     *
     * @param user The user for whom the JWT tokens are to be generated.
     * @return A {@link LoggedUserRespDTO} containing the generated access and refresh tokens.
     * @throws IllegalArgumentException If the user information is invalid or the token generation fails.
     */
    LoggedUserRespDTO generatePairOfTokens(@NotNull User user);

    /**
     * Regenerates a pair of JWT tokens (access and refresh tokens) using a valid refresh token.
     * This method is used to refresh the user's authentication after the access token has expired.
     *
     * @param refreshToken The refresh token used to regenerate the pair of JWT tokens.
     * @return A {@link LoggedUserRespDTO} containing the newly generated access and refresh tokens.
     * @throws IllegalArgumentException If the provided refresh token is invalid, expired, or cannot be used to regenerate tokens.
     */
    LoggedUserRespDTO regeneratePairOfTokens(@NotBlank String refreshToken);
}
