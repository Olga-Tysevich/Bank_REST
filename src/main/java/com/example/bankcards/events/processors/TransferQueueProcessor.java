package com.example.bankcards.events.processors;

import com.example.bankcards.config.queues.QueuesConf;
import com.example.bankcards.dto.redis.TransferMessageDTO;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.impl.TransferServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service that processes transfer messages from Redis queues.
 * <p>
 * This service handles the processing of transfer events by interacting with the Redis queues:
 * - It retrieves transfer messages from the transfer queue, processes them using the {@link TransferService},
 * and handles success or failure scenarios.
 * - It also processes confirmed transfers, potentially for notifications or additional processing.
 * - In case of stuck messages in the processing queue, it requeues them for retry.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferQueueProcessor {
    private final QueuesConf queuesConf;
    private final TransferService transferService;
    private final RedisTemplate<String, TransferMessageDTO> redisTemplate;
    private final String processing_key = ":processing";

    /**
     * Processes the transfer queue by moving messages from the transfer queue to the processing queue,
     * and then attempts to make a transfer via the {@link TransferService}.
     * <p>
     * If the transfer is successful, the transfer message is removed from the processing queue.
     * If the transfer fails, the transfer message remains in the processing queue, and the transfer is canceled.
     * </p>
     * This method is scheduled to run at a fixed rate, as configured in the application properties.
     */
    @Scheduled(fixedRateString = "${spring.schedule.queueProcessor.fixedRate:10000}")
    public void processQueue() {

        String transferQueueName = queuesConf.getTransferQueueName();
        String processingQueue = transferQueueName + processing_key;

        TransferMessageDTO transferDTO = redisTemplate.opsForList()
                .rightPopAndLeftPush(transferQueueName, processingQueue);

        if (Objects.nonNull(transferDTO)) {
            try {
                transferService.makeTransfer(transferDTO);
                redisTemplate.opsForList().remove(processingQueue, 1, transferDTO);
                log.info("Successfully processed and removed transferDTO: {}", transferDTO);
            } catch (Exception e) {
                log.error("Failed to process transferDTO, will stay in processing queue: {}", transferDTO, e);
                ((TransferServiceImpl) transferService).cancelTransfer(transferDTO);
            }
        }

        String confirmedTransferQueueName = queuesConf.getConfirmedTransferQueueName();
        TransferMessageDTO confirmedTransferDTO = redisTemplate.opsForList().rightPop(confirmedTransferQueueName);
        if (Objects.nonNull(confirmedTransferDTO)) {
            //Тут можно слать уведомления юзеру или еще что-то
            log.info("Processing confirmed transferDTO from queue: {}", confirmedTransferDTO);
        }

    }

    /**
     * Requeues stuck messages that remain in the processing queue after a failed attempt.
     * <p>
     * This method retrieves all messages from the processing queue and moves them back to the transfer queue for retry.
     * </p>
     * This method is scheduled to run at a fixed delay, as configured in the application properties.
     */
    @Scheduled(fixedDelayString = "${spring.schedule.queueProcessor.retry:10000}")
    public void requeueStuckMessages() {
        String transferQueueName = queuesConf.getTransferQueueName();
        String processingQueue = transferQueueName + processing_key;

        List<TransferMessageDTO> stuckMessages = redisTemplate.opsForList().range(processingQueue, 0, -1);
        if (stuckMessages != null) {
            for (TransferMessageDTO dto : stuckMessages) {
                redisTemplate.opsForList().remove(processingQueue, 1, dto);
                redisTemplate.opsForList().leftPush(transferQueueName, dto);
                log.warn("Requested stuck message back to main queue: {}", dto);
            }
        }
    }
}