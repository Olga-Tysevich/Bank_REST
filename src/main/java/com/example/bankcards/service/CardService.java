package com.example.bankcards.service;

import com.example.bankcards.dto.api.req.AddCardDTO;
import com.example.bankcards.dto.api.req.SearchReq;
import com.example.bankcards.dto.api.req.filters.CardSearchFilter;
import com.example.bankcards.dto.api.resp.CardDTO;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.dto.api.resp.PageResp;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;

/**
 * The {@code CardService} interface defines the contract for managing card operations.
 * Implementations of this interface provide methods for creating, updating, deleting, and retrieving card details,
 * as well as managing the card's status.
 */
public interface CardService {

    /**
     * Creates a new card based on the provided request details.
     *
     * @param request The details required for creating a new card, encapsulated in an {@link AddCardDTO} object.
     * @return The ID of the created card.
     * @throws IllegalArgumentException If the card creation fails due to invalid data or other issues.
     */
    Long createCard(@NotNull AddCardDTO request);

    /**
     * Updates an existing card based on the provided update request details.
     *
     * @param request The details required to update the card, encapsulated in an {@link UpdateCardDTO} object.
     * @return The ID of the updated card.
     * @throws IllegalArgumentException If the update fails due to invalid data or other issues.
     */
    Long updateCard(@NotNull UpdateCardDTO request);

    /**
     * Deletes an existing card by its ID.
     *
     * @param id The ID of the card to be deleted.
     * @return The ID of the deleted card.
     * @throws IllegalArgumentException If the card cannot be found or deleted.
     */
    Long deleteCard(@NotNull Long id);

    /**
     * Sets the status of an existing card.
     *
     * @param id     The ID of the card whose status is to be set.
     * @param status The new status to be applied to the card.
     * @return The ID of the card with the updated status.
     * @throws IllegalArgumentException If the card cannot be found or the status change is invalid.
     */
    Long setCardStatus(@NotNull Long id, @NotNull CardStatus status);

    /**
     * Retrieves a card by its ID.
     *
     * @param id The ID of the card to be retrieved.
     * @return A {@link CardDTO} containing the card's details.
     * @throws IllegalArgumentException If the card cannot be found.
     */
    CardDTO getCard(@NotNull Long id);

    /**
     * Retrieves a paginated list of cards based on the provided search criteria.
     *
     * @param req The search request, containing filtering and pagination parameters encapsulated in a {@link SearchReq} object.
     * @return A {@link PageResp} containing a list of {@link CardDTO} objects and pagination information.
     * @throws IllegalArgumentException If the search request is invalid or no cards match the criteria.
     */
    PageResp<CardDTO> getCards(@NotNull SearchReq<CardSearchFilter> req);

    /**
     * Marks expired cards as expired.
     * This method processes all cards that have expired and marks them with an expired status.
     */
    void markExpiredCards();
}
