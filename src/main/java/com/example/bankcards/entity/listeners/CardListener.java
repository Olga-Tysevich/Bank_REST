package com.example.bankcards.entity.listeners;

import com.example.bankcards.entity.Card;
import com.example.bankcards.service.EncryptionService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.PostLoad;

@Component
public class CardListener {

    private static EncryptionService encryptionService;

    @Autowired
    public CardListener(EncryptionService encryptionService) {
        CardListener.encryptionService = encryptionService;
    }

    @PrePersist
    @PreUpdate
    public void encryptNumber(Card card) {
        if (card.getNumber() != null) {
            card.setNumber(encryptionService.encrypt(card.getNumber()));
        }
    }

    @PostLoad
    public void decryptNumber(Card card) {
        if (card.getNumber() != null) {
            card.setNumber(encryptionService.decrypt(card.getNumber()));
        }
    }

}
