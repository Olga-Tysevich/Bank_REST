package com.example.bankcards.security;

import com.example.bankcards.service.impl.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.example.bankcards.util.Constants.TOKEN_HEADER;
import static com.example.bankcards.util.Constants.TOKEN_TYPE;

/**
 * A custom filter that intercepts HTTP requests to handle JWT-based authentication.
 * <p>
 * This filter checks if the request contains a valid JWT token in the header. If the token is valid,
 * it authenticates the user and sets the authentication context in the {@link SecurityContextHolder}.
 * If the token is invalid or missing, the request is either skipped (if it matches an ignored URL pattern)
 * or an authentication failure response is returned with status {@link HttpServletResponse#SC_UNAUTHORIZED}.
 * <p>
 * This filter extends {@link OncePerRequestFilter} to ensure it is applied only once per request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * JwtProvider bean.
     * @see JwtProvider
     */
    private final JwtProvider jwtProvider;
    /**
     * UserDetailsService bean.
     * @see com.example.bankcards.service.impl.UserDetailsServiceImpl
     */
    private final UserDetailsService userDetailsService;

    @Value("${spring.app.web.ignoredUrls:*}")
    private List<String> ignoredUrls;

    /**
     * Determines whether the filter should not be applied for the given request.
     * <p>
     * The filter is skipped if the request path matches any of the ignored URL patterns.
     * The ignored URLs are configurable through the {@code spring.app.web.ignoredUrls} property.
     *
     * @param request the HTTP request to check
     * @return {@code true} if the filter should be skipped for this request, {@code false} otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = request.getRequestURI();
        log.debug("path: {}", path);
        log.debug("ignoredUrls: {}", ignoredUrls);
        log.debug("shouldNotFilter: {}", ignoredUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path)));
        return ignoredUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Filters the request to authenticate the user based on the JWT token present in the request header.
     * <p>
     * This method checks the Authorization header for a valid JWT token, validates the token, and extracts the
     * username from the token. If the token is valid and the username is not already authenticated, it sets the
     * {@link SecurityContext} with an authenticated {@link UsernamePasswordAuthenticationToken}.
     * If the token is invalid or authentication fails, an unauthorized error is returned with HTTP status
     * {@link HttpServletResponse#SC_UNAUTHORIZED}.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to pass the request to the next filter
     * @throws ServletException if an error occurs during filter processing
     * @throws IOException if an I/O error occurs while processing the request or response
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(TOKEN_HEADER);
            if (StringUtils.isBlank(header) || !StringUtils.startsWith(header, TOKEN_TYPE)) {
                log.debug("No JWT token found in request header or invalid token type. Skipping authentication.");
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = header.substring(TOKEN_TYPE.length());
            if (!jwtProvider.validateAccessToken(jwt)) {
                log.warn("Invalid JWT token detected. Unauthorized access attempt.");
                throw new UnavailableException("Unauthorized access");
            }

            String username = jwtProvider.getAccessClaims(jwt).getSubject();
            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Authenticated user '{}'. Setting security context.", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (!auth.getPrincipal().equals(username)) {
                    log.error("JWT token belongs to a different user. Invalid token for user '{}'.", username);
                    throw new JwtException("Invalid token");
                }
            }

            filterChain.doFilter(request, response);

        } catch (UnavailableException | JwtException e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(e.getMessage());
        }
    }
}