package com.example.bankcards.entity.enums;

/**
 * Represents available card statuses in the system.
 */
public enum CardStatus {
    ACTIVE,
    BLOCKED,
    EXPIRED;

    /**
     * Checking card status
     * @param cardStatus current card status
     * @return false if status is ACTIVE
     */
    public static boolean isLockedForTransfer(CardStatus cardStatus) {
        return cardStatus == CardStatus.EXPIRED || cardStatus == CardStatus.BLOCKED;
    }

}