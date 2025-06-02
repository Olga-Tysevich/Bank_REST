package com.example.bankcards.dto.api.crypto;

import static com.example.bankcards.util.Constants.CARD_NUMBER_DIGIT_FOR_DISPLAY;

/**
 * A record representing an encrypted card number and its masked version.
 * <p>
 * This record contains two fields: {@code encrypted}, which holds the encrypted version of the card number,
 * and {@code cardMask}, which contains the masked version of the card number with only the last few digits visible.
 * The card mask is useful for displaying only the last few digits of a card number while keeping the rest masked for security purposes.
 * </p>
 */
public record EncryptedCardNumber(String encrypted, String cardMask) {
    /**
     * Generates a masked version of a plain card number.
     * <p>
     * This method takes a plain card number (as a string) and generates a masked version of it by replacing most digits
     * with asterisks ('*') and leaving the last few digits visible. The number of visible digits is determined by the constant
     * {@link com.example.bankcards.util.Constants#CARD_NUMBER_DIGIT_FOR_DISPLAY}.
     * </p>
     *
     * @param plainCardNumber The plain card number as a string.
     * @return A masked version of the card number with the last few digits visible and the rest masked with asterisks.
     */
    public static String generateCardMask(String plainCardNumber) {
        int maskLength = plainCardNumber.length() - CARD_NUMBER_DIGIT_FOR_DISPLAY;

        StringBuilder maskedPart = new StringBuilder();
        for (int i = 0; i < maskLength; i++) {
            maskedPart.append("*");
            if ((i + 1) % CARD_NUMBER_DIGIT_FOR_DISPLAY == 0 && i != maskLength - 1) {
                maskedPart.append(" ");
            }
        }

        String visiblePart = plainCardNumber.substring(maskLength);

        return maskedPart.append(visiblePart).toString();
    }

}

