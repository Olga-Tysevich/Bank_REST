package com.example.bankcards.service;


import com.example.bankcards.dto.api.req.UserLoginDTO;
import com.example.bankcards.dto.api.resp.LoggedUserDTO;

/**
 * Interface for authenticating and logging in users.
 */
public interface AuthService {
    /**
     * Authenticates the user based on the provided credentials and generates a pair of JWT tokens (access and refresh tokens).
     *
     * @param req the request object containing the user's login details, such as username and password.
     * @return {@link LoggedUserDTO} containing the generated JWT tokens (access and refresh tokens) for the logged-in user.
     * @throws org.springframework.security.authentication.BadCredentialsException if authentication fails.
     */
    LoggedUserDTO loginUser(UserLoginDTO req);

    /**
     * Regenerates a pair of JWT tokens (access and refresh tokens) using the provided refresh token.
     *
     * @param refreshToken the refresh token used to regenerate the access and refresh tokens.
     * @return {@link LoggedUserDTO} containing the new pair of JWT tokens.
     * @throws IllegalArgumentException if the refresh token is invalid or expired.
     */
    LoggedUserDTO reLoginUser(String refreshToken);

}