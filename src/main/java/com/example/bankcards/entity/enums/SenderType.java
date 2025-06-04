package com.example.bankcards.entity.enums;

/**
 * Enum representing the type of sender in a financial transaction.
 * <p>This enum defines two types of senders:</p>
 * <ul>
 *     <li>{@link #COMPANY} - Represents a company as the sender.</li>
 *     <li>{@link #INDIVIDUAL} - Represents an individual as the sender.</li>
 * </ul>
 */
public enum SenderType {
    /**
     * Represents a company as the sender in a transaction.
     */
    COMPANY,

    /**
     * Represents an individual as the sender in a transaction.
     */
    INDIVIDUAL
}