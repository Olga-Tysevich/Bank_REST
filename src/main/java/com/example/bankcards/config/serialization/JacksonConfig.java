package com.example.bankcards.config.serialization;

import com.example.bankcards.config.serialization.deserializers.CardTypeCodeDeserializer;
import com.example.bankcards.config.serialization.deserializers.EncryptedCardNumberDeserializer;
import com.example.bankcards.config.serialization.deserializers.EnrollDTODeserializer;
import com.example.bankcards.dto.api.crypto.EncryptedCardNumber;
import com.example.bankcards.dto.api.req.EnrollDTO;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.service.EncryptionService;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Jackson serialization and deserialization.
 * <p>
 * This class configures a custom deserializer for {@link EncryptedCardNumber} to be used during the deserialization process.
 * It registers an {@link EncryptedCardNumberDeserializer} to handle the deserialization of encrypted card numbers.
 * </p>
 * <p>
 * The deserializer uses the {@link EncryptionService} for encrypting card numbers, and it is registered as a Spring
 * bean to be used in the Jackson context during the application's runtime.
 * </p>
 */
@Configuration
public class JacksonConfig {
    /**
     * Registers a custom Jackson module with an {@link EncryptedCardNumberDeserializer}.
     * <p>
     * This method creates a {@link SimpleModule} and adds the custom deserializer for the {@link EncryptedCardNumber}
     * class. The deserializer uses an {@link EncryptionService} for encryption operations.
     * </p>
     *
     * @param encryptionService The {@link EncryptionService} used for encryption of card numbers.
     * @return The {@link SimpleModule} with the custom deserializer for {@link EncryptedCardNumber}.
     */
    @Bean
    public SimpleModule encryptionModule(EncryptionService encryptionService) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(EncryptedCardNumber.class, new EncryptedCardNumberDeserializer(encryptionService));
        module.addDeserializer(CardType.class, new CardTypeCodeDeserializer());
        module.addDeserializer(EnrollDTO.class, new EnrollDTODeserializer());
        return module;
    }

}
