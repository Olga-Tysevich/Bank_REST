package com.example.bankcards.repository;

import com.example.bankcards.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface provides methods for accessing and manipulating RefreshToken entities in the database.
 *
 * @see RefreshToken
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}