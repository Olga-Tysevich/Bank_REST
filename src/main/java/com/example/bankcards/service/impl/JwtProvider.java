package com.example.bankcards.service.impl;

import com.example.bankcards.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.example.bankcards.util.Constants.ID_CLAIM;
import static com.example.bankcards.util.Constants.ROLE_CLAIM;

/**
 * This class provides methods for generating access tokens and refresh tokens.
 */
@Component
@Slf4j
public class JwtProvider {
    /**
     * The JWT access key secret is used to sign and verify access tokens.
     */
    @Value("${spring.application.security.jwt.access-key.secret}")
    private String jwtAccessSecret;
    /**
     * The JWT refresh key secret is used to sign and verify refresh tokens.
     */
    @Value("${spring.application.security.jwt.refresh-key.secret}")
    private String jwtRefreshSecret;
    /**
     * The JWT access expiration time determines how long an access token is valid for.
     */
    @Value("${spring.application.security.jwt.access-key.expiration-time}")
    private Integer jwtAccessExpirationTime;
    /**
     * The JWT refresh expiration time determines how long a refresh token is valid for.
     */
    @Value(("${spring.application.security.jwt.refresh-key.expiration-time}"))
    private Integer jwtRefreshExpirationTime;

    /**
     * Generates JWT access token.
     *
     * @param user represents a user object.
     * @return String representation of JWT access token.
     * @see User
     */
    public String generateAccessToken(@NotNull User user) {
        final Date accessExpiration = generateAccessExpiration(jwtAccessExpirationTime);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessExpiration)
                .signWith(getJwtAccessSecret())
                .claim(ID_CLAIM, user.getId())
                .claim(ROLE_CLAIM, user.getAuthorities())
                .compact();
    }

    /**
     * Generates JWT refresh token.
     *
     * @param user represents a user object.
     * @return String representation of JWT refresh token.
     * @see User
     */
    public String generateRefreshToken(@NotNull User user) {
        final Date refreshExpiration = generateAccessExpiration(jwtRefreshExpirationTime);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(refreshExpiration)
                .signWith(getJwtRefreshSecret())
                .compact();

    }

    /**
     * Validates JWT access token.This method calls {@link JwtProvider#validateToken(String, Key)}.
     *
     * @param accessToken String representation of JWT access token.
     * @return true if the JWT access token is valid, false otherwise.
     */
    public boolean validateAccessToken(@NotNull String accessToken) {
        return validateToken(accessToken, getJwtAccessSecret());
    }

    /**
     * Validates JWT refresh token.This method calls {@link JwtProvider#validateToken(String, Key)}.
     *
     * @param refreshToken String representation of JWT refresh token.
     * @return true if the JWT refresh token is valid, false otherwise.
     */
    public boolean validateRefreshToken(@NotNull String refreshToken) {
        return validateToken(refreshToken, getJwtRefreshSecret());
    }

    /**
     * Validates a JWT token using the provided secret key.
     *
     * @param token     String representation of JWT token to validate.
     * @param secretKey The secret key used to sign the JWT token.
     * @return true if the token is valid, false otherwise.
     */
    private boolean validateToken(@NotNull String token, @NotNull Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token format: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is null or empty or only whitespace: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Incorrect JWT token signature");
        }

        return false;
    }

    /**
     * Extracts claim from JWT access token.
     *
     * @param token String representation of JWT access token.
     * @return The claims contained in the JWT access token.
     */
    public Claims getAccessClaims(@NotNull String token) {
        return getClaims(token, getJwtAccessSecret());
    }

    /**
     * Extracts claim from JWT refresh token.
     *
     * @param token String representation of JWT refresh token.
     * @return The claims contained in the JWT refresh token.
     */
    public Claims getRefreshClaims(@NotNull String token) {
        return getClaims(token, getJwtRefreshSecret());
    }

    /**
     * Retrieves the secret key used for creating JWT access tokens.
     *
     * @return The secret key for JWT access tokens.
     */
    private SecretKey getJwtAccessSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    /**
     * Retrieves the secret key used for creating JWT refresh tokens.
     *
     * @return The secret key for JWT refresh tokens.
     */
    private SecretKey getJwtRefreshSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    /**
     * Parses the given JWT token using the provided secret key and returns the claims from the token.
     *
     * @param token     The JWT token to be parsed.
     * @param secretKey The secret key used for parsing the JWT token.
     * @return Claims contained in the JWT token.
     * @throws NullPointerException if token or secretKey is null.
     */
    private Claims getClaims(@NotNull String token, @NotNull Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates an expiration date for access based on the current date/time and the specified expiration time in minutes.
     *
     * @param expirationTime The expiration time in minutes.
     * @return Date The expiration date for access.
     */
    private Date generateAccessExpiration(@NotNull Integer expirationTime) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(expirationTime)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(accessExpirationInstant);
    }

}