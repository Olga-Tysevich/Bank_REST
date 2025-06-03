package com.example.bankcards.events.processors;

import com.example.bankcards.config.queues.QueuesConf;
import com.example.bankcards.dto.redis.CardBlockRequestCreatedMessageDTO;
import com.example.bankcards.service.CardBlockRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service responsible for processing card block request messages from a Redis-backed queue.
 * <p>
 * This class contains scheduled tasks that:
 * <ul>
 *   <li>Consume and process messages from a Redis queue</li>
 *   <li>Handle retry logic for stuck or failed messages</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardBlockRequestQueueProcessor {
    /**
     * Configuration class that provides the names of the Redis queues.
     */
    private final QueuesConf queuesConf;

    /**
     * Service responsible for handling card block request business logic.
     */
    private final CardBlockRequestService cardBlockRequestService;

    /**
     * RedisTemplate for accessing and modifying Redis queues containing
     * {@link CardBlockRequestCreatedMessageDTO} messages.
     */
    private final RedisTemplate<String, CardBlockRequestCreatedMessageDTO> redisTemplate;

    /**
     * Suffix used to indicate a "processing" queue that temporarily holds in-progress messages.
     */
    private final String processing_key = ":processing";

    /**
     * Scheduled task that runs at a fixed rate to process card block request messages.
     * <p>
     * It moves a message from the main queue to a processing queue,
     * processes the message, and upon successful completion removes it
     * from the processing queue. If an error occurs, the message remains
     * in the processing queue for later retry.
     * </p>
     */
    @Scheduled(fixedRateString = "${spring.schedule.queueProcessor.fixedRate:10000}")
    public void processQueue() {

        String cardBlockRequestCreated = queuesConf.getCardBlockRequestCreated();
        String processingQueue = cardBlockRequestCreated + processing_key;

        CardBlockRequestCreatedMessageDTO cardBlockRequestCreatedMessageDTO = redisTemplate.opsForList()
                .rightPopAndLeftPush(cardBlockRequestCreated, processingQueue);

        if (Objects.nonNull(cardBlockRequestCreatedMessageDTO)) {
            try {
                cardBlockRequestService.assignAdministrator(cardBlockRequestCreatedMessageDTO);
                redisTemplate.opsForList().remove(processingQueue, 1, cardBlockRequestCreatedMessageDTO);
                log.info("Successfully processed and removed cardBlockRequestCreatedMessageDTO: {}", cardBlockRequestCreatedMessageDTO);
            } catch (Exception e) {
                log.error("Failed to process cardBlockRequestCreatedMessageDTO, will stay in processing queue: {}", cardBlockRequestCreatedMessageDTO, e);
            }
        }

    }

    /**
     * Scheduled task that retries stuck messages left in the processing queue.
     * <p>
     * It moves all messages from the processing queue back into the main queue
     * so they can be reprocessed on the next execution of {@link #processQueue()}.
     * </p>
     */
    @Scheduled(fixedDelayString = "${spring.schedule.queueProcessor.retry:10000}")
    public void requeueStuckMessages() {
        String cardBlockRequestCreatedQueueName = queuesConf.getCardBlockRequestCreated();
        String processingQueue = cardBlockRequestCreatedQueueName + processing_key;

        List<CardBlockRequestCreatedMessageDTO> stuckMessages = redisTemplate.opsForList().range(processingQueue, 0, -1);
        if (stuckMessages != null) {
            for (CardBlockRequestCreatedMessageDTO dto : stuckMessages) {
                redisTemplate.opsForList().remove(processingQueue, 1, dto);
                redisTemplate.opsForList().leftPush(cardBlockRequestCreatedQueueName, dto);
                log.warn("Requested stuck message back to main queue: {}", dto);
            }
        }
    }
}