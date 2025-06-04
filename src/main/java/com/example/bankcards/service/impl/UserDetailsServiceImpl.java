package com.example.bankcards.service.impl;

import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link UserDetailsService} interface.
 * Provides user details for Spring Security authentication by loading user information from the database.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * Repository for accessing user data from the database.
     * @see UserRepository
     */
    private final UserRepository userRepository;

    /**
     * Loads user details by username for authentication purposes.
     *
     * @param username the username of the user to be loaded.
     * @return {@link UserDetails} the user details for the user with the specified username.
     * @throws UsernameNotFoundException if the user with the given username is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}