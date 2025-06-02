package com.example.bankcards.config.crypto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for storing adjustable application crypto parameters. Stores constants set externally
 */
@Component
@Getter
public class CryptoConfig {
    @Value("${spring.application.security.crypto.type}")
    private String cryptoType;
    @Value("${spring.application.security.crypto.key}")
    private String secretKey;
}
