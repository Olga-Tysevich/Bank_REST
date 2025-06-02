package com.example.bankcards.controller;

import com.example.bankcards.dto.api.req.UserLoginReqDTO;
import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import com.example.bankcards.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.example.bankcards.util.Constants.REFRESH_TOKEN_KEY;
import static com.example.bankcards.util.Constants.TOKEN_TYPE;

/**
 * REST controller for handling user authentication operations.
 * Provides endpoints for login, logout, and token refresh functionality.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    /**
     * Authenticates a user and generates access/refresh tokens.
     *
     * @param req User credentials (username and password)
     * @return ResponseEntity containing access token and setting refresh token cookie
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginReqDTO req) {
        LoggedUserRespDTO respBody = authService.loginUser(req);
        ResponseCookie cookie = createRefreshTokenCookie(respBody.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(respBody);
    }

    /**
     * Invalidates the current refresh token by clearing client cookie.
     *
     * @return Empty response with cleared refresh token cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_KEY, "")
                .httpOnly(true)
                .path("/v1/api/auth/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    /**
     * Generates new access/refresh tokens using a valid refresh token.
     *
     * @param request HTTP request containing refresh token (cookie or Authorization header)
     * @return ResponseEntity with new tokens or UNAUTHORIZED error
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);

        if (Objects.isNull(refreshToken) || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token provided");
        }

        LoggedUserRespDTO newTokens = authService.reLoginUser(refreshToken);
        ResponseCookie cookie = createRefreshTokenCookie(newTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(newTokens);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        // 1. From cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (REFRESH_TOKEN_KEY.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. From header Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(authHeader) && authHeader.startsWith(TOKEN_TYPE)) {
            return authHeader.substring(TOKEN_TYPE.length());
        }

        return null;
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/v1/api/auth/refresh")
                .sameSite("Strict")
                .build();
    }
}