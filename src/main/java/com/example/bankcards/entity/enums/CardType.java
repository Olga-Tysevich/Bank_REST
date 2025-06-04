package com.example.bankcards.entity.enums;


import lombok.Getter;

/**
 * Enum representing different types of credit/debit cards with their corresponding number patterns.
 * <p>
 * The card number regex patterns are based on the following scheme:
 * - 2200, 2201, 2202: Bank-specific pattern.
 * - Visa: Starts with '4' and has a length of 13-16 digits.
 * - MasterCard: Starts with '5' followed by digits between 1-5 for the second digit and has a total length of 16 digits.
 * - American Express: Starts with '3' followed by '47' and has a length of 15 digits.
 * <p>
 * For the regex used, refer to the constant <code>CARD_NUMBER_REGEX</code>.
 */
@Getter
public enum CardType {
    /**
     * Represents a Visa card, which starts with '4' and has 13 to 16 digits.
     */
    VISA(1, "Visa", "411111", "4[0-9]{12,15}"),

    /**
     * Represents a MasterCard, which starts with '5' and the second digit is between '1' and '5'.
     * The total length of the card number is 16 digits.
     */
    MASTERCARD(2, "MasterCard", "511111", "5[1-5][0-9]{14}"),

    /**
     * Represents an American Express card, which starts with '3' followed by '47'.
     * The total length of the card number is 15 digits.
     */
    AMERICAN_EXPRESS(3, "American Express", "371111", "3[47][0-9]{13}"),

    /**
     * Represents a card type starting with '2200', '2201', or '2202'.
     */
    BANK_SPECIFIC(4, "Bank Specific", "220220", "220[0-2][0-9]{12}");

    public static final String MASKED_CARD_PATTERN = "^(\\*{4} ?)*\\d{4}$";
    public static final int DIGIT_IN_ONE_SECTION = 4;

    private final int typeCode;
    private final String name;
    private final String prefix;
    private final String regex;

    /**
     * Constructor for the enum, initializing the name and regex.
     *
     * @param typeCode The application type code of the card type.
     * @param name     The name of the card type.
     * @param regex    The regex pattern corresponding to the card type's number.
     */
    CardType(int typeCode, String name, String prefix, String regex) {
        this.typeCode = typeCode;
        this.name = name;
        this.prefix = prefix;
        this.regex = regex;
    }

    /**
     * This constant represents a general regex pattern that includes all card types defined in the enum.
     * <p>
     * The pattern combines individual card type regex patterns and can be used for validation purposes.
     * </p>
     */
    public static final String CARD_NUMBER_REGEX = "^(220[0-2][0-9]{12}|4[0-9]{12,15}|5[1-5][0-9]{14}|3[47][0-9]{13})$";

    /**
     * Gets the minimum code value for the card types.
     * <p>
     * This method returns the smallest code value, which is used as a starting value
     * when working with card types that require numeric values. In this case, it is fixed to 1.
     * </p>
     *
     * @return the minimum code value (always 1).
     */
    public static int getMinCodeValue() {
        return 1;
    }

    /**
     * Gets the maximum code value based on the number of card types in the enum.
     * <p>
     * This method returns the total number of values defined in the {@link CardType} enum.
     * It represents the upper limit of possible code values for the card types.
     * </p>
     *
     * @return the maximum code value (the number of card types in the enum).
     */
    public static int getMaxCodeValue() {
        return CardType.values().length;
    }

    /**
     * Generates a masked version of a plain card number.
     * <p>
     * This method takes a plain card number (as a string) and generates a masked version of it by replacing most digits
     * with asterisks ('*') and leaving the last few digits visible. The number of visible digits is determined by the constant
     * </p>
     *
     * @param plainCardNumber The plain card number as a string.
     * @return A masked version of the card number with the last few digits visible and the rest masked with asterisks.
     */
    public static String generateCardMask(String plainCardNumber) {
        int maskLength = plainCardNumber.length() - DIGIT_IN_ONE_SECTION;

        StringBuilder maskedPart = new StringBuilder();
        for (int i = 0; i < maskLength; i++) {
            maskedPart.append("*");
            if ((i + 1) % DIGIT_IN_ONE_SECTION == 0 && i != maskLength - 1) {
                maskedPart.append(" ");
            }
        }

        String visiblePart = plainCardNumber.substring(maskLength);

        return maskedPart.append(visiblePart).toString();
    }
}
