package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * This interface provides methods for accessing and manipulating Card entities in the database.
 *
 * @see Card
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * The method allows you to get the owner's ID by the card ID
     * @param cardId ID of the card you are looking for
     * @return card owner id
     */
    @Query("select c.owner.id from Card c where c.id = :cardId")
    Optional<Long> getOwnerIdById(@NotNull @Param("cardId") Long cardId);

    /**
     * The method allows you to find a card by ID, if its balance is sufficient for writing off
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


    @Query("SELECT c.id FROM Card c WHERE c.number = :encryptedCardNumber")
    Optional<Long> findIdByEncryptedCardNumber(@Param("encryptedCardNumber") String encryptedCardNumber);

}
