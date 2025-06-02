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

    @Override
    public LoggedUserRespDTO loginUser(UserLoginReqDTO req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = (User) userDetailsService.loadUserByUsername(req.getUsername());

        return jwtService.generatePairOfTokens(user);
    }

       @Override
    public LoggedUserRespDTO reLoginUser(String refreshToken) {
        return jwtService.regeneratePairOfTokens(refreshToken);
    }

}