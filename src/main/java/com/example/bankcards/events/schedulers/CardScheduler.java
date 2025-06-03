package com.example.bankcards.events.schedulers;

import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler responsible for managing scheduled tasks related to bank cards.
 * <p>
 * This component runs a scheduled task to mark expired cards at regular intervals.
 * It interacts with the {@link CardService} to update expired cards.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CardScheduler {

    private final CardService cardService;

    /**
     * Marks expired cards at a fixed rate.
     * <p>
     * This method is triggered at a regular interval as specified by the `fixedRate` property in the application configuration.
     * It attempts to mark all expired cards as expired by calling the {@link CardService#markExpiredCards()} method.
     * If an exception occurs during the process, it is logged with an error message.
     * </p>
     */
    @Scheduled(fixedRateString = "${spring.schedule.timing.cards.expiry:86400000}",
            initialDelayString = "${spring.schedule.timing.cards.initialDelay:43200000}")
    public void markExpiredCards() {
        try {
            cardService.markExpiredCards();
        } catch (Exception e) {
            log.error("Exception occurred while marking expired cards: ", e);
        }
    }
}
