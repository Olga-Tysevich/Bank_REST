package com.example.bankcards.service.impl;

import com.example.bankcards.dto.api.req.notifications.CardBlockRequestNotificationDTO;
import com.example.bankcards.dto.api.req.notifications.NotificationReq;
import com.example.bankcards.dto.redis.CardBlockRequestCreatedMessageDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.notifications.CardBlockRequest;
import com.example.bankcards.exception.ProhibitedException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.util.PrincipalExtractor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Service implementation for managing card block requests.
 * <p>
 * Handles the creation of card block notifications, user ownership validation,
 * card status updates, and event publication for further asynchronous processing.
 * Also, responsible for assigning administrators to card block requests.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CardBlockRequestServiceImpl implements CardBlockRequestService {
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    /**
     * The ApplicationEventPublisher bean
     *
     * @see ApplicationEventPublisher
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates a new card block request notification.
     * <p>
     * Validates that the current user is the owner of the card. If so, it creates a new {@link CardBlockRequest},
     * changes the card status to {@link CardStatus#BLOCKED}, and publishes a {@link CardBlockRequestCreatedMessageDTO}
     * event to be processed asynchronously (e.g., for admin assignment).
     * </p>
     *
     * @param notificationReq the incoming request wrapped in a generic {@link NotificationReq}
     * @return the ID of the created card block request
     * @throws ProhibitedException if the current user is not the owner of the card
     * @throws NullPointerException if the card or user is not found
     */
    @Transactional
    @Override
    public Long createNotification(@NotNull NotificationReq notificationReq) {

        CardBlockRequestNotificationDTO cardBlockRequestNotificationDTO =
                NotificationReq.castToConcreteNotification(notificationReq, CardBlockRequestNotificationDTO.class);

        User user = PrincipalExtractor.getCurrentUser();
        Long currentUserId = Objects.nonNull(user) ? user.getId() : null;

        Long cardId = cardBlockRequestNotificationDTO.getCardId();

        Card card = cardRepository.findById(cardId).orElseThrow();

        Long ownerId = card.getOwner().getId();

        if (currentUserId == null || !currentUserId.equals(ownerId)) {
            throw new ProhibitedException(currentUserId);
        }

        String note = Objects.requireNonNullElse(cardBlockRequestNotificationDTO.getNote(), "");


        CardBlockRequest cardBlockRequest = CardBlockRequest.builder()
                .fromUser(user)
                .note(note)
                .card(card)
                .build();
        cardBlockRequestRepository.save(cardBlockRequest);
        // Логика тут должна зависеть от требований, для примера просто ставим статус "Заблокировано"
        card.setStatus(CardStatus.BLOCKED);

        CardBlockRequestCreatedMessageDTO messageDTO = CardBlockRequestCreatedMessageDTO.builder()
                .id(cardBlockRequest.getId())
                .createdAt(cardBlockRequest.getCreatedAt())
                .userId(user.getId())
                .cardId(card.getId())
                .note(note)
                .build();

        eventPublisher.publishEvent(messageDTO);

        return cardBlockRequest.getId();
    }

    /**
     * Assigns an administrator to a card block request for further manual review or processing.
     * <p>
     * Ensures that the request hasn't been confirmed or already assigned. If valid, it assigns a randomly selected
     * administrator to the request.
     * </p>
     *
     * @param cardBlockRequestCreatedMessageDTO DTO containing the card block request details
     * @throws IllegalStateException if the request is already confirmed or has an admin assigned
     */
    @Transactional
    @Override
    public void assignAdministrator(@NotNull CardBlockRequestCreatedMessageDTO cardBlockRequestCreatedMessageDTO) {

        Long cardBlockRequestId = cardBlockRequestCreatedMessageDTO.getId();
        CardBlockRequest cardBlockRequest = cardBlockRequestRepository.findById(cardBlockRequestId).orElseThrow();

        if (cardBlockRequest.getIsConfirmed()) {
            throw new IllegalStateException("Card block request already confirmed! Id: " + cardBlockRequestId);
        }

        if (Objects.nonNull(cardBlockRequest.getAppointedAdmin())) {
            throw new IllegalStateException("The admin has already been appointed for CardBlockRequest with id: " + cardBlockRequestId);
        }

        //Логика назначения в данном задании опущена, просто для примера найдем рандомного админа
        User admin = userRepository.findRandomAdmin().orElseThrow();
        cardBlockRequest.setAppointedAdmin(admin);

        cardBlockRequestRepository.save(cardBlockRequest);

        //Далее можно было бы реализовать проверку всех назначеных администраторов, получение уведомлений и т.д.
    }

}
