package com.example.bankcards.events.schedulers;

import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler responsible for managing scheduled tasks related to bank transfers.
 * <p>
 * This component runs a scheduled task to cancel pending transfers at regular intervals.
 * It interacts with the {@link TransferService} to cancel transfers that are still pending.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BankRestScheduler {
    /**
     * The transfer service bean
     *
     * @see TransferService
     */
    private final TransferService transferService;

    /**
     * Cancels pending transfers at a fixed rate.
     * <p>
     * This method is triggered at a regular interval as specified by the `fixedRate` property in the application configuration.
     * The task attempts to cancel any pending transfers by calling the {@link TransferService#cancelPendingTransfers()} method.
     * If an exception occurs during the cancellation process, it is logged with an error message.
     * </p>
     *
     * @see TransferService#cancelPendingTransfers()
     */
    @Scheduled(fixedRateString = "${spring.schedule.timing.transfer.cancel:86400000}",
            initialDelayString = "${spring.schedule.timing.transfer.initialDelay:43200000}")
    public void cancelTransfers() {
        try {
            transferService.cancelPendingTransfers();
        } catch (Exception e) {
            log.error("Exception occurred while cancelling transfers: ", e);
        }
    }

}
