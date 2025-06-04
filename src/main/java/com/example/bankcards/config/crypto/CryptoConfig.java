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
    @Value("${spring.application.security.crypto.algorithm}")
    private String cryptoAlgorithm;

    @Value("${spring.application.security.crypto.transformation}")
    private String cryptoTransformation;

    @Value("${spring.application.security.crypto.key}")
    private String cryptoSecretKey;

    public String getCryptoAlgorithmWithTransformation() {
        return cryptoAlgorithm + "/" + cryptoTransformation;
    }
}
