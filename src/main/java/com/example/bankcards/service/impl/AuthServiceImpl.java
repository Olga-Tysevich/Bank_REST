package com.example.bankcards.service.impl;

import com.example.bankcards.dto.api.req.UserLoginReqDTO;
import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link AuthService} interface.
 * Handles user authentication and token generation.
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class AuthServiceImpl implements AuthService {
    /**
     * Service responsible for loading user details from the database.
     *
     * @see UserDetailsServiceImpl
     */
    private final UserDetailsService userDetailsService;
    /**
     * The {@link AuthenticationManager} used to authenticate users.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Service responsible for generating JWT tokens.
     *
     * @see JwtServiceImpl
     */
    private final JwtService jwtService;

    /**
     * Authenticates the user based on the provided credentials and generates a pair of JWT tokens (access and refresh tokens).
     *
     * @param req the request object containing the user's login details, such as username and password.
     * @return {@link LoggedUserRespDTO} containing the generated JWT tokens (access and refresh tokens) for the logged-in user.
     * @throws org.springframework.security.authentication.BadCredentialsException if authentication fails.
     */
    @Override
    public LoggedUserRespDTO loginUser(UserLoginReqDTO req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = (User) userDetailsService.loadUserByUsername(req.getUsername());

        return jwtService.generatePairOfTokens(user);
    }

    /**
     * Regenerates a pair of JWT tokens (access and refresh tokens) using the provided refresh token.
     *
     * @param refreshToken the refresh token used to regenerate the access and refresh tokens.
     * @return {@link LoggedUserRespDTO} containing the new pair of JWT tokens.
     * @throws IllegalArgumentException if the refresh token is invalid or expired.
     */
    @Override
    public LoggedUserRespDTO reLoginUser(String refreshToken) {
        return jwtService.regeneratePairOfTokens(refreshToken);
    }

}