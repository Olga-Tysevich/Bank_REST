package com.example.bankcards.config.serialization.deserializers;

import com.example.bankcards.entity.enums.CardType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Custom deserializer for the {@link CardType} enum.
 * <p>
 * This deserializer is responsible for converting an integer card type code to the corresponding
 * {@link CardType} enum. It also logs the process of deserialization, including any errors that occur
 * if an invalid card type code is provided.
 * </p>
 * <p>
 * Logging is performed at two levels:
 * - <code>DEBUG</code>: Logs the card type code being deserialized and the successful matching of the code to a {@link CardType}.
 * - <code>ERROR</code>: Logs an error if an invalid card type code is provided, which does not correspond to any of the {@link CardType} values.
 * </p>
 */
@Slf4j
public class CardTypeCodeDeserializer extends JsonDeserializer<CardType> {

    /**
     * Deserialize the JSON value into a corresponding {@link CardType} based on the provided card type code.
     * <p>
     * This method checks if the card type code provided in the JSON is valid and maps it to a corresponding
     * {@link CardType} enum constant. If the code is invalid, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param p the JSON parser to read the card type code from.
     * @param ctxt the deserialization context.
     * @return The corresponding {@link CardType} enum based on the card type code.
     * @throws IOException if an I/O error occurs during deserialization.
     * @throws JsonProcessingException if a processing error occurs during deserialization.
     */
    @Override
    public CardType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        int cardTypeCode = p.getIntValue();

        log.debug("Deserializing card type code: {}", cardTypeCode);

        for (CardType cardType : CardType.values()) {
            if (cardType.getTypeCode() == cardTypeCode) {
                log.debug("Successfully matched card type code {} to {}", cardTypeCode, cardType);
                return cardType;
            }
        }

        log.error("Invalid card type code: {}", cardTypeCode);

        throw new IllegalArgumentException("Invalid card type code: " + cardTypeCode);
    }
}
