package com.example.bankcards.service;

import com.example.bankcards.dto.api.req.notifications.NotificationReq;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for handling notification creation.
 * <p>
 * This interface defines the contract for creating notifications. Implementing services
 * should provide the logic to process and store notifications based on the incoming request.
 * </p>
 */
public interface NotificationService {

    /**
     * Creates a notification based on the provided request.
     * <p>
     * This method processes the incoming notification request, validates it, and persists
     * the notification data. It returns the ID of the created notification for further processing.
     * </p>
     *
     * @param notificationReq the notification request containing the details of the notification
     * @return the ID of the created notification
     * @throws NullPointerException if the notification request is invalid or not provided
     */
    Long createNotification(@NotNull NotificationReq notificationReq);

}
