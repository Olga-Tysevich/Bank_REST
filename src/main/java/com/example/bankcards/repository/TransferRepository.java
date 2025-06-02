package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface provides methods for accessing and manipulating Transfer entities in the database.
 *
 * @see Transfer
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    /**
     * Retrieves a page of transfer IDs where the transfer status is {@code PENDING}
     * and the creation date is earlier than the current date.
     * <p>
     * This query is typically used to identify outdated pending transfers
     * that may require further processing, cancellation, or notification.
     *
     * @param pageable the pagination information, including page number, size, and sorting
     * @return a {@link Page} containing the IDs of matching {@link Transfer} entities
     */
    @Query("SELECT t.id FROM Transfer t WHERE t.status = 'PENDING' AND t.createdAt < CURRENT_DATE")
    Page<Long> findTransferIdsWithStatusPendingAndCreatedAToToday(Pageable pageable);

    /**
     * Retrieves a {@link Transfer} entity by its ID with a pessimistic write lock.
     * <p>
     * This method is typically used to ensure exclusive access to the transfer record
     * for update operations in concurrent environments. The {@code PESSIMISTIC_WRITE}
     * lock prevents other transactions from reading or modifying the record
     * until the current transaction is completed.
     * </p>
     *
     * @param id the ID of the transfer to retrieve and lock
     * @return an {@link Optional} containing the {@link Transfer} if found,
     *         or an empty {@link Optional} if no entity with the given ID exists
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transfer t WHERE t.id = :id")
    Optional<Transfer> findByIdForUpdate(@Param("id") Long id);

}
