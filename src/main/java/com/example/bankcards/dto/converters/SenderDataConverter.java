package com.example.bankcards.dto.converters;

import com.example.bankcards.entity.enums.SourceOfFunds;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

/**
 * JPA AttributeConverter for converting a {@link Map} of {@link SourceOfFunds.SenderDataKey} keys and {@link String} values
 * to a JSON {@link String} and vice versa for storing and retrieving in/from the database.
 * <p>
 * The converter serializes the map into a JSON string when storing the data in the database,
 * and deserializes the JSON string back into the map when retrieving the data from the database.
 * </p>
 *
 * @see SourceOfFunds.SenderDataKey
 * @see ObjectMapper
 */
@Converter
public class SenderDataConverter implements AttributeConverter<Map<SourceOfFunds.SenderDataKey, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts the given {@link Map} of {@link SourceOfFunds.SenderDataKey} to a JSON {@link String} to be stored in the database.
     *
     * @param attribute the map to be converted
     * @return the JSON string representation of the map
     * @throws IllegalArgumentException if an error occurs during conversion
     */
    @Override
    public String convertToDatabaseColumn(Map<SourceOfFunds.SenderDataKey, String> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    /**
     * Converts the given JSON string from the database back into a {@link Map} of {@link SourceOfFunds.SenderDataKey} keys and {@link String} values.
     *
     * @param dbData the JSON string to be converted
     * @return the map representation of the JSON string
     * @throws IllegalArgumentException if an error occurs during conversion
     */
    @Override
    public Map<SourceOfFunds.SenderDataKey, String> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading JSON to map", e);
        }
    }
}
