package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardType;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.example.bankcards.util.Constants.ID_CANNOT_BE_NULL;

/**
 * This interface provides methods for accessing and manipulating Card entities in the database.
 *
 * @see Card
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    /**
     * The method allows you to get the owner's ID by the card ID
     *
     * @param cardId ID of the card you are looking for
     * @return card owner id
     */
    @Query("select c.owner.id from Card c where c.id = :cardId")
    Optional<Long> getOwnerIdById(@NotNull @Param("cardId") Long cardId);

    /**
     * The method allows you to find a card by ID, if its balance is sufficient for writing off
     *
     * @param cardId ID of the card you are looking for
     * @param amount amount to be written off
     * @return the desired card
     */
    @Query("SELECT c FROM Card c WHERE c.id = :cardId " +
            "AND c.balance >= :amount")
    Optional<Card> findByIdAndSufficientBalance(@NotNull @Param("cardId") Long cardId, @NotNull @Param("amount") BigDecimal amount);

    /**
     * The method checks whether the card is blocked (status BLOCKED or EXPIRED) by the passed ID.
     *
     * @param cardId ID of the card being checked
     * @return true if the card with the given ID is blocked or expired
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Card c WHERE c.id = :cardId AND (c.status = 'BLOCKED' OR c.status = 'EXPIRED')")
    boolean isBlockedOrExpired(@NotNull @Param("cardId") Long cardId);

    /**
     * Retrieves the ID of a card by its encrypted card number.
     *
     * @param encryptedCardNumber the encrypted card number
     * @return an {@link Optional} containing the card ID if found, or empty otherwise
     */
    @Query("SELECT c.id FROM Card c WHERE c.number = :encryptedCardNumber")
    Optional<Long> findIdByEncryptedCardNumber(@Param("encryptedCardNumber") String encryptedCardNumber);

    /**
     * Retrieves the last card (by ID descending) of a specific type owned by a given user,
     * applying a pessimistic write lock.
     *
     * @param ownerId the ID of the card owner
     * @param type the type of the card
     * @return an {@link Optional} containing the last card if found, or empty otherwise
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.owner.id = :ownerId AND c.type = :type ORDER BY c.id DESC LIMIT 1")
    Optional<Card> findLastByCardType(@Param("ownerId") Long ownerId, @Param("type") CardType type);

    /**
     * Retrieves the ID of a card by its plain (unencrypted) card number.
     *
     * @param cardNumber the plain card number
     * @return an {@link Optional} containing the card ID if found, or empty otherwise
     */
    @Query("SELECT c.id FROM Card c WHERE c.number = :cardNumber")
    Optional<Long> findIdByNumber(@Param("cardNumber") String cardNumber);

    /**
     * Increments the balance of an active card by the specified amount.
     *
     * @param cardId the ID of the card
     * @param amount the amount to add to the balance
     * @return the number of updated rows (0 if the card is not active or not found)
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Card c SET c.balance = c.balance + :amount WHERE c.id = :cardId AND c.status = 'ACTIVE'")
    int addToBalance(@Param("cardId") Long cardId, @Param("amount") BigDecimal amount);

    /**
     * Checks whether a card exists by its ID and the owner's ID.
     *
     * @param id the ID of the card
     * @param ownerId the ID of the owner
     * @return true if such a card exists, false otherwise
     */
    boolean existsByIdAndOwnerId(@NotNull Long id, @NotNull Long ownerId);

    /**
     * Retrieves a paginated list of cards by the owner's ID.
     *
     * @param ownerId the ID of the card owner
     * @param pageable the pagination information
     * @return a {@link Page} of {@link Card} objects belonging to the owner
     */
    Page<Card> findAllByOwner_Id(@NotNull(message = ID_CANNOT_BE_NULL) Long ownerId, Pageable pageable);

    /**
     * Finds cards that are expired.
     * This method returns cards that are past the expiration date but have not been marked as expired.
     *
     * @param pageable the pageable object to handle pagination
     * @param expirationDate the date that is compared against the expiration date of the card
     * @param status the status of the card to filter out already expired cards
     * @return a page of expired cards
     */
    Page<Card> findCardsByExpirationBeforeAndStatusNot(Pageable pageable, LocalDate expirationDate, String status);



}
