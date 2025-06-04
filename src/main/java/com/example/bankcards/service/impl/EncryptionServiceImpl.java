package com.example.bankcards.service.impl;

import com.example.bankcards.config.crypto.CryptoConfig;
import com.example.bankcards.service.EncryptionService;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The {@code EncryptionServiceImpl} class provides encryption and decryption functionalities.
 * It uses the Cipher class from Java's cryptography library to encrypt and decrypt data
 * using a specified algorithm and secret key.
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {

    /**
     * The CryptoConfig bean
     *
     * @see CryptoConfig
     */
    private final CryptoConfig cryptoConfig;

    private final SecretKeySpec keySpec;

    /**
     * Constructor that initializes the EncryptionServiceImpl with the necessary cryptographic configuration.
     *
     * @param cryptoConfig The cryptographic configuration (algorithm type and secret key).
     */
    public EncryptionServiceImpl(CryptoConfig cryptoConfig) {
        this.cryptoConfig = cryptoConfig;

        String algorithm = cryptoConfig.getCryptoAlgorithm();
        String secretKey = cryptoConfig.getCryptoSecretKey();

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        if ("AES".equals(algorithm) && (!(keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32))) {
            throw new IllegalArgumentException("Invalid AES key length: " + keyBytes.length + " bytes. Must be 16, 24, or 32.");
        }

        this.keySpec = new SecretKeySpec(keyBytes, algorithm);
    }

    /**
     * Encrypts the given input string using the specified cryptographic algorithm and secret key.
     *
     * @param input The plaintext string to be encrypted.
     * @return The encrypted string, encoded in Base64.
     * @throws RuntimeException If the encryption process fails.
     */
    public String encrypt(String input) {
        try {
            String algorithm = cryptoConfig.getCryptoAlgorithmWithTransformation();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts the given encrypted string using the specified cryptographic algorithm and secret key.
     *
     * @param input The encrypted string (Base64 encoded).
     * @return The decrypted plaintext string.
     * @throws RuntimeException If the decryption process fails.
     */
    public String decrypt(String input) {
        try {
            String algorithm = cryptoConfig.getCryptoAlgorithmWithTransformation();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(input));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

}
