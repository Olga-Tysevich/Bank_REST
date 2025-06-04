package com.example.bankcards.service.impl;

import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidRefreshTokenException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bankcards.util.Constants.TOKEN_CANNOT_BE_NULL_OR_EMPTY;


/**
 * Service class that provides implementation for generating and regenerating JWT tokens.
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class JwtServiceImpl implements JwtService {
    /**
     * RefreshTokenRepository bean.
     *
     * @see RefreshTokenRepository
     */
    private final RefreshTokenRepository refreshTokenRepository;
    /**
     * JwtProvider bean.
     *
     * @see JwtProvider
     */
    private final JwtProvider jwtProvider;
    /**
     * UserRepository bean.
     *
     * @see UserRepository
     */
    private final UserRepository userRepository;

    /**
     * Generates a pair of JWT tokens (access token and refresh token) for the specified user.
     *
     * @param user the user for whom the JWT tokens will be generated.
     * @return {@link LoggedUserRespDTO} containing the generated pair of JWT tokens (access token and refresh token).
     */
    @Override
    public LoggedUserRespDTO generatePairOfTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        saveRefreshToken(user.getUsername(), refreshToken);
        return LoggedUserRespDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    /**
     * Regenerates a pair of JWT tokens (access token and refresh token) using the provided refresh token.
     *
     * @param refreshToken the refresh token used to regenerate the access and refresh tokens.
     * @return {@link LoggedUserRespDTO} containing the newly regenerated pair of JWT tokens.
     * @throws IllegalArgumentException if the provided refresh token is invalid or expired.
     */
    @Override
    public LoggedUserRespDTO regeneratePairOfTokens(@Valid @NotBlank(message = TOKEN_CANNOT_BE_NULL_OR_EMPTY)
                                                    String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        String username = jwtProvider.getRefreshClaims(refreshToken).getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with username: " + username));
        return generatePairOfTokens(user);
    }

    /**
     * Saves the refresh token for the given email.
     *
     * @param username The email of the user for whom the token is saved.
     * @param token    The refresh token to be saved.
     */
    private void saveRefreshToken(@Valid @Email String username,
                                  @Valid @NotBlank(message = TOKEN_CANNOT_BE_NULL_OR_EMPTY) String token) {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException("Cannot find user with username: " + username);
        }
        refreshTokenRepository.save(new RefreshToken(username, token));
    }

}