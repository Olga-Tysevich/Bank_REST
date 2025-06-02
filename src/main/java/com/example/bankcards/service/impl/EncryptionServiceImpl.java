package com.example.bankcards.service.impl;

import com.example.bankcards.config.crypto.CryptoConfig;
import com.example.bankcards.service.EncryptionService;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionServiceImpl implements EncryptionService {

    private final CryptoConfig cryptoConfig;

    private final SecretKeySpec keySpec;

    public EncryptionServiceImpl(CryptoConfig cryptoConfig) {
        this.cryptoConfig = cryptoConfig;
        String algorithm = cryptoConfig.getCryptoType();
        String secretKey = cryptoConfig.getSecretKey();
        this.keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
    }

    public String encrypt(String input) {
        try {
            String algorithm = cryptoConfig.getCryptoType();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String input) {
        try {
            String algorithm = cryptoConfig.getCryptoType();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(input));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

}
