package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface provides methods for accessing and manipulating User entities in the database.
 *
 * @see User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a User by their username.
     *
     * @param username the username of the user to search for
     * @return an Optional containing the found User, or empty if no User with the given username exists
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a User exists with the specified username.
     *
     * @param username the username to check for existence
     * @return true if a User with the given username exists, otherwise false
     */
    boolean existsByUsername(String username);

}