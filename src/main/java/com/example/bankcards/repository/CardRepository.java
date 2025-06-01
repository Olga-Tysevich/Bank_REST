package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface provides methods for accessing and manipulating Card entities in the database.
 *
 * @see Card
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
}
