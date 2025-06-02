package com.example.bankcards.service;

public interface EncryptionService {
    String encrypt(String input);
    String decrypt(String input);
}
