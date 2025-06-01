package com.example.bankcards.service;

import com.example.bankcards.dto.api.resp.LoggedUserDTO;
import com.example.bankcards.entity.User;

/**
 * Service interface responsible for generating and regenerating JWT tokens.
 * Provides methods for issuing and refreshing JWT tokens for user authentication.
 */
public interface JwtService {
    /**
     * Generates a pair of JWT tokens (access token and refresh token) for the specified user.
     *
     * @param user the user for whom the JWT tokens will be generated.
     * @return {@link LoggedUserDTO} containing the generated pair of JWT tokens (access token and refresh token).
     */
    LoggedUserDTO generatePairOfTokens(User user);

    /**
     * Regenerates a pair of JWT tokens (access token and refresh token) using the provided refresh token.
     *
     * @param refreshToken the refresh token used to regenerate the access and refresh tokens.
     * @return {@link LoggedUserDTO} containing the newly regenerated pair of JWT tokens.
     * @throws IllegalArgumentException if the provided refresh token is invalid or expired.
     */
    LoggedUserDTO regeneratePairOfTokens(String refreshToken);

}