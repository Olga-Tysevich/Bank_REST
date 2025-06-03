package com.example.bankcards.config.serialization.deserializers;

import com.example.bankcards.dto.api.req.EnrollDTO;
import com.example.bankcards.entity.enums.SenderType;
import com.example.bankcards.entity.enums.SourceOfFunds;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Custom deserializer for {@link EnrollDTO} objects.
 *
 * <p>This deserializer handles the deserialization of JSON input into an {@link EnrollDTO} instance.
 * It performs the following operations:</p>
 * <ul>
 *     <li>Parses standard fields such as cardId, sourceOfFunds, senderType, and amount</li>
 *     <li>Parses a map of additional sender information from the "enrollmentInformation" field</li>
 *     <li>Validates that all required fields are present in the enrollment information,
 *         based on the sender type ({@link SenderType#INDIVIDUAL} or {@link SenderType#COMPANY})</li>
 * </ul>
 *
 * <p>If any required fields are missing or if the JSON contains unknown enum values for keys,
 * it logs a warning or throws an exception accordingly.</p>
 */
@Slf4j
public class EnrollDTODeserializer extends JsonDeserializer<EnrollDTO> {
    /**
     * Mapping of required enrollment information keys based on the sender type.
     */
    private static final Map<SenderType, List<SourceOfFunds.SenderDataKey>> REQUIRED_FIELDS = Map.of(
            SenderType.INDIVIDUAL, SourceOfFunds.SenderDataKey.mandatoryForIndividual(),
            SenderType.COMPANY, SourceOfFunds.SenderDataKey.mandatoryForCompany()
    );

    /**
     * Deserializes JSON data into an {@link EnrollDTO} object.
     *
     * @param jp   the JSON parser
     * @param ctxt the deserialization context
     * @return the deserialized {@link EnrollDTO} object
     * @throws IOException              if an error occurs during deserialization
     * @throws IllegalArgumentException if required enrollment fields are missing
     */
    @Override
    public EnrollDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);

        Long cardId = node.get("cardId").asLong();
        SourceOfFunds source = SourceOfFunds.valueOf(node.get("sourceOfFunds").asText());
        SenderType senderType = SenderType.valueOf(node.get("senderType").asText());
        BigDecimal amount = new BigDecimal(node.get("amount").asText());

        JsonNode mapNode = node.get("enrollmentInformation");
        Map<SourceOfFunds.SenderDataKey, String> info = new HashMap<>();
        if (mapNode != null && mapNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = mapNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                try {
                    SourceOfFunds.SenderDataKey key = SourceOfFunds.SenderDataKey.valueOf(entry.getKey());
                    info.put(key, entry.getValue().asText());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown enrollmentInformation key: {} for card id: {}", entry.getKey(), cardId);
                }
            }
        }

        List<SourceOfFunds.SenderDataKey> requiredKeys = REQUIRED_FIELDS.getOrDefault(senderType, List.of());

        for (SourceOfFunds.SenderDataKey key : requiredKeys) {
            if (!info.containsKey(key)) {
                throw new IllegalArgumentException("Missing required enrollmentInformation field for " +
                        senderType + ": " + key + " for card id: " + cardId);
            }
        }

        return EnrollDTO.builder()
                .cardId(cardId)
                .sourceOfFunds(source)
                .senderType(senderType)
                .enrollmentInformation(info)
                .amount(amount)
                .build();
    }
}
