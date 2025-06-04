package com.example.bankcards.repository;

import com.example.bankcards.entity.notifications.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This interface provides methods for accessing and manipulating CardBlockRequest entities in the database.
 *
 * @see CardBlockRequest
 */
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
}
