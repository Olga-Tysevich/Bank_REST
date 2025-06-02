package com.example.bankcards.repository;

import com.example.bankcards.entity.BackupAccount;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface provides methods for accessing and manipulating user backup account entities in the database.
 *
 * @see BackupAccount
 */
@Repository
public interface BackupAccountRepository extends JpaRepository<BackupAccount, Long> {

    /**
     * Find account by owner id
     * @param ownerId the owner id to look up
     * @return BackupAccount
     */
    BackupAccount findByOwner_Id(@NotNull Long ownerId);

    /**
     * Checks if an Account exists with the specified ownerId.
     *
     * @param ownerId the owner id to check for existence
     * @return true if an Account with the given owner id exists, otherwise false
     */
    boolean existsByOwner_Id(@NotNull Long ownerId);
}
