package com.example.bankcards.config.auth;

import com.example.bankcards.security.ApiAccessDeniedHandler;
import com.example.bankcards.security.ForbiddenEntryPoint;
import com.example.bankcards.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * WebSecurityConfig is a configuration class that sets up security filters and custom authentication settings for the application.
 * It configures authentication mechanisms, user roles, and access control for various HTTP requests.
 * It also configures Cross-Origin Resource Sharing (CORS) settings and custom exception handling for unauthorized access.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthFilter authFilter;

    private final UserDetailsService userDetailsService;

    @Value("${spring.application.web.allowedSources:*}")
    private List<String> allowedSources;

    @Value("${spring.application.web.allowedMethods:*}")
    private List<String> allowedMethods;

    @Value("${spring.application.web.allowedHeaders:*}")
    private List<String> allowedHeaders;

    @Value("${spring.application.web.ignoredUrls:*}")
    private List<String> ignoredUrls;

    /**
     * Configures the SecurityFilterChain for handling HTTP requests.
     * It disables CSRF protection, enables CORS with custom settings, sets session management to stateless,
     * and configures authentication provider and filters.
     *
     * @param httpSecurity the HttpSecurity object used to configure security for HTTP requests.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Security Filter Chain ignored urls {}", ignoredUrls);

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(allowedSources);
                    corsConfig.setAllowedMethods(allowedMethods);
                    corsConfig.setAllowedHeaders(allowedHeaders);
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(ignoredUrls.toArray(new String[0])).permitAll()
                        .requestMatchers("/v1/api/card/add", "/v1/api/card/admin/update", "/v1/api/card/admin/{id}/{status}/update", "/v1/api/card/admin/{id}/delete").hasRole("ADMIN")
                        .requestMatchers("/v1/api/card/get/**", "/v1/api/card/block").hasRole("USER")
                        .requestMatchers("/v1/api/auth/login", "/v1/api/auth/refresh").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(entryPoint()));
        return httpSecurity.build();
    }

    /**
     * Configures the AuthenticationProvider bean to handle user authentication with password encoding.
     * It uses a DaoAuthenticationProvider that interacts with the UserDetailsService and applies BCrypt password encoding.
     *
     * @return the configured AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Provides the AuthenticationManager bean to manage the authentication process.
     *
     * @param config the AuthenticationConfiguration object to obtain the AuthenticationManager.
     * @return the AuthenticationManager.
     * @throws Exception if an error occurs while retrieving the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the PasswordEncoder bean to use BCrypt for hashing and verifying passwords.
     *
     * @return the BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the ApiAccessDeniedHandler bean for handling access denied exceptions in the application.
     *
     * @return the ApiAccessDeniedHandler instance.
     */
    @Bean
    public ApiAccessDeniedHandler accessDeniedHandler() {
        return new ApiAccessDeniedHandler();
    }

    /**
     * Provides the ForbiddenEntryPoint bean for handling unauthorized access to resources.
     *
     * @return the ForbiddenEntryPoint instance.
     */
    @Bean
    public ForbiddenEntryPoint entryPoint() {
        return new ForbiddenEntryPoint();
    }

}
