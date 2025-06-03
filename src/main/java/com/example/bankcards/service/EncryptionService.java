package com.example.bankcards.service;

/**
 * Interface provides encryption and decryption functionalities.
 */
public interface EncryptionService {
    /**
     * Encrypts the given input string.
     *
     * @param input The plaintext string to be encrypted.
     * @return The encrypted string.
     */
    String encrypt(String input);

    /**
     * Decrypts the given encrypted string.
     *
     * @param input The encrypted string to be decrypted.
     * @return The decrypted plaintext string.
     */
    String decrypt(String input);
}
