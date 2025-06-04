package com.example.bankcards.dto.api.crypto;

/**
 * A record representing an encrypted card number and its masked version.
 * <p>
 * This record contains two fields: {@code encrypted}, which holds the encrypted version of the card number,
 * and {@code cardMask}, which contains the masked version of the card number with only the last few digits visible.
 * The card mask is useful for displaying only the last few digits of a card number while keeping the rest masked for security purposes.
 * </p>
 */
public record EncryptedCardNumber(String encrypted, String cardMask) {

}

