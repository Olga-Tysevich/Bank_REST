package com.example.bankcards.events.listeners;

import com.example.bankcards.config.queues.QueuesConf;
import com.example.bankcards.dto.redis.TransferMessageDTO;
import com.example.bankcards.events.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Listener for handling bank transfer events.
 * <p>
 * This class listens to two types of events: {@link Events.TransferEvent} and {@link Events.TransferConfirmed}.
 * It processes the transfer-related data and pushes the corresponding {@link TransferMessageDTO}
 * to specific Redis queues for further processing.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferEventListener {
    /**
     * The queues config
     * @see QueuesConf
     */
    private final QueuesConf queuesConf;
    private final RedisTemplate<String, TransferMessageDTO> redisTemplate;

    /**
     * Handles {@link Events.TransferEvent}.
     * <p>
     * This method is invoked when a transfer event occurs. It logs the details of the transfer and pushes the
     * {@link TransferMessageDTO} to the Redis queue for processing.
     * </p>
     *
     * @param event the transfer event containing the transfer details.
     */
    @EventListener
    public void handleTransferEvent(Events.TransferEvent event) {
        TransferMessageDTO transferMessageDTO = event.transferDTO();
        log.info("Received TransferEvent: TransferDTO [id={}, amount={}, from={}, to={}]",
                transferMessageDTO.getId(), transferMessageDTO.getAmount(), transferMessageDTO.getFromCardId(), transferMessageDTO.getToCardId());

        try {
            String transferQueueName = queuesConf.getTransferQueueName();
            redisTemplate.opsForList().leftPush(transferQueueName, transferMessageDTO);
            log.info("TransferDTO successfully pushed to the Redis transfer queue.");
        } catch (Exception e) {
            log.error("Failed to push TransferDTO to Redis queue: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles {@link Events.TransferConfirmed}.
     * <p>
     * This method is invoked when a transfer confirmation event occurs. It logs the details of the confirmed transfer
     * and pushes the {@link TransferMessageDTO} to the Redis confirmed transfer queue.
     * </p>
     *
     * @param event the transfer confirmation event containing the transfer details.
     */
    @EventListener
    public void handleTransferConfirmedEvent(Events.TransferConfirmed event) {
        TransferMessageDTO trantransferMessageDTOferDTO = event.transferDTO();

        log.info("Received TransferConfirmed event: TransferDTO [id={}, amount={}, from={}, to={}]",
                trantransferMessageDTOferDTO.getId(), trantransferMessageDTOferDTO.getAmount(),
                trantransferMessageDTOferDTO.getFromCardId(), trantransferMessageDTOferDTO.getToCardId());

        try {
            String confirmedTransferQueueName = queuesConf.getConfirmedTransferQueueName();
            redisTemplate.opsForList().leftPush(confirmedTransferQueueName, trantransferMessageDTOferDTO);
            log.info("TransferDTO successfully pushed to the Redis confirmed transfer queue.");
        } catch (Exception e) {
            log.error("Failed to push TransferDTO to Redis confirmed transfer queue: {}", e.getMessage(), e);
        }
    }

}