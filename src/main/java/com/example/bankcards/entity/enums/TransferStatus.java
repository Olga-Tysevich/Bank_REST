package com.example.bankcards.entity.enums;
/**
 * Represents available transfer statuses in the system.
 * <p>This enum defines three types of transfer status:</p>
 * <ul>
 *     <li>{@link #PENDING} - Transfer status in progress.</li>
 *     <li>{@link #COMPLETED} - Completed Transfer Status.</li>
 *     <li>{@link #FAILED} - Status of a transfer that failed to complete.</li>
 * </ul>
 */
public enum TransferStatus {
    PENDING,
    COMPLETED,
    FAILED
}