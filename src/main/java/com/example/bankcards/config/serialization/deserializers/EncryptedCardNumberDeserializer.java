package com.example.bankcards.config.serialization.deserializers;

import com.example.bankcards.dto.api.crypto.EncryptedCardNumber;
import com.example.bankcards.service.EncryptionService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.example.bankcards.util.Constants.CARD_NUMBER_REGEX;

/**
 * Custom Jackson deserializer for {@link EncryptedCardNumber}.
 * <p>
 * This deserializer takes a plain card number string, validates its format using a regular expression,
 * generates a masked version of the card number, encrypts the card number, and then returns an {@link EncryptedCardNumber}
 * object containing the encrypted card number and its masked version.
 * </p>
 * <p>
 * The deserializer requires an {@link EncryptionService} for encryption purposes and uses a regular expression
 * to validate the card number format. If the format is invalid, an {@link InvalidFormatException} is thrown.
 * </p>
 */
public class EncryptedCardNumberDeserializer extends StdDeserializer<EncryptedCardNumber> {

    private final EncryptionService encryptionService;
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile(CARD_NUMBER_REGEX);

    /**
     * Constructs a new {@link EncryptedCardNumberDeserializer} with the provided {@link EncryptionService}.
     *
     * @param encryptionService The service used to encrypt the card number.
     */
    public EncryptedCardNumberDeserializer(EncryptionService encryptionService) {
        super(EncryptedCardNumber.class);
        this.encryptionService = encryptionService;
    }

    /**
     * Deserializes a plain card number into an {@link EncryptedCardNumber} object.
     * <p>
     * This method takes a plain card number in string format, validates it against a regular expression pattern,
     * generates a masked version of the card number, and encrypts the original card number.
     * </p>
     *
     * @param p The JSON parser used to parse the input value.
     * @param ctxt The deserialization context.
     * @return An {@link EncryptedCardNumber} object containing the encrypted card number and its masked version.
     * @throws IOException If an I/O error occurs during deserialization.
     * @throws InvalidFormatException If the provided card number does not match the expected format.
     */
    @Override
    public EncryptedCardNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String plainCardNumber = p.getValueAsString();

        if (Objects.isNull(plainCardNumber)) return null;

        if (!CARD_NUMBER_PATTERN.matcher(plainCardNumber).matches()) {
            throw new InvalidFormatException(p, "Invalid card number format", plainCardNumber, EncryptedCardNumber.class);
        }

        String cardMask = EncryptedCardNumber.generateCardMask(plainCardNumber);

        String encrypted = encryptionService.encrypt(plainCardNumber);

        return new EncryptedCardNumber(encrypted, cardMask);
    }

}

