package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface provides methods for accessing and manipulating Transfer entities in the database.
 *
 * @see Transfer
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
