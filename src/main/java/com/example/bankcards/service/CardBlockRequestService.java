package com.example.bankcards.service;

import com.example.bankcards.dto.redis.CardBlockRequestCreatedMessageDTO;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for handling card block requests.
 * <p>
 * This interface extends the {@link NotificationService} interface and provides functionality
 * for managing card block requests. It defines methods for assigning administrators to card
 * block requests and interacting with the corresponding data transfer objects (DTOs).
 * </p>
 */
public interface CardBlockRequestService extends NotificationService {

    /**
     * Assigns an administrator to a card block request for further manual review or processing.
     * <p>
     * This method is called to assign a user with administrative privileges to manage the card
     * block request. The administrator is typically responsible for reviewing and confirming
     * the request before taking further actions.
     * </p>
     *
     * @param cardBlockRequestCreatedMessageDTO DTO containing the details of the created card block request
     * @throws IllegalStateException if the request is already confirmed or has an administrator assigned
     */
    void assignAdministrator(@NotNull CardBlockRequestCreatedMessageDTO cardBlockRequestCreatedMessageDTO);

}
