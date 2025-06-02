package com.example.bankcards.service;


import com.example.bankcards.dto.api.req.UserLoginReqDTO;
import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Interface for authenticating and logging in users.
 */
public interface AuthService {
    /**
     * Authenticates the user based on the provided credentials and generates a pair of JWT tokens (access and refresh tokens).
     *
     * @param req the request object containing the user's login details, such as username and password.
     * @return {@link LoggedUserRespDTO} containing the generated JWT tokens (access and refresh tokens) for the logged-in user.
     * @throws org.springframework.security.authentication.BadCredentialsException if authentication fails.
     */
    LoggedUserRespDTO loginUser(@NotNull UserLoginReqDTO req);

    /**
     * Regenerates a pair of JWT tokens (access and refresh tokens) using the provided refresh token.
     *
     * @param refreshToken the refresh token used to regenerate the access and refresh tokens.
     * @return {@link LoggedUserRespDTO} containing the new pair of JWT tokens.
     * @throws IllegalArgumentException if the refresh token is invalid or expired.
     */
    LoggedUserRespDTO reLoginUser(@NotBlank String refreshToken);

}