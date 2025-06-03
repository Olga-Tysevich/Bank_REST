package com.example.bankcards.events.listeners;

import com.example.bankcards.config.queues.QueuesConf;
import com.example.bankcards.dto.redis.CardBlockRequestCreatedMessageDTO;
import com.example.bankcards.events.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Listener component for handling {@link Events.CardBlockRequestCreated} events.
 * <p>
 * When a card block request event is received, this component serializes the associated
 * {@link CardBlockRequestCreatedMessageDTO} and pushes it into a Redis queue.
 * </p>
 *
 * @see QueuesConf for queue configuration
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CardBlockRequestEventListener {
    /**
     * The queues config
     *
     * @see QueuesConf
     */
    private final QueuesConf queuesConf;
    /**
     * RedisTemplate used for pushing {@link CardBlockRequestCreatedMessageDTO} objects into Redis.
     */
    private final RedisTemplate<String, CardBlockRequestCreatedMessageDTO> redisTemplate;

    /**
     * Handles the {@link Events.CardBlockRequestCreated} event.
     * <p>
     * This method is triggered automatically when a new CardBlockRequestCreated event is published.
     * It pushes the corresponding DTO to a Redis queue for further processing.
     * </p>
     *
     * @param event the event containing the card block request data
     */
    @EventListener
    public void handleTransferEvent(Events.CardBlockRequestCreated event) {
        CardBlockRequestCreatedMessageDTO messageDTO = event.cardBlockRequestDTO();
        log.info("Received CardBlockRequestCreated event: CardBlockRequestCreatedMessageDTO id={}",
                messageDTO.getId());

        try {
            String transferQueueName = queuesConf.getCardBlockRequestCreated();
            redisTemplate.opsForList().leftPush(transferQueueName, messageDTO);
            log.info("CardBlockRequestCreatedMessageDTO successfully pushed to the Redis transfer queue.");
        } catch (Exception e) {
            log.error("Failed to push CardBlockRequestCreatedMessageDTO to Redis queue: {}", e.getMessage(), e);
        }
    }

}