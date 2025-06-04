package com.example.bankcards.entity.listeners;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.service.EncryptionService;
import com.example.bankcards.util.validators.impl.CardBalanceValidator;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.persistence.PostLoad;

/**
 * This class is a JPA listener for the {@link Card} entity. It performs actions
 * such as validation, encryption, and decryption of sensitive card data before
 * persisting or updating a card, and after loading the card from the database.
 * <p>
 * The listener ensures that:
 * <ul>
 *   <li>The card number is valid before saving or updating.</li>
 *   <li>The card number is encrypted before being stored, unless already encrypted.</li>
 *   <li>The card number is decrypted after loading from the database.</li>
 *   <li>The card balance is validated before saving or updating.</li>
 * </ul>
 *
 * The listener also uses the {@link EncryptionService} to handle the encryption
 * and decryption of card numbers, and the {@link CardBalanceValidator} to validate
 * the balance of the card.
 */
@Component
public class CardListener {
    /**
     * The EncryptionService bean.
     * @see EncryptionService
     */
    private static EncryptionService encryptionService;
    /**
     * The CardBalanceValidator bean.
     * @see CardBalanceValidator
     */
    private static CardBalanceValidator balanceValidator;

    /**
     * Constructor for initializing the {@link CardListener} with the necessary
     * {@link EncryptionService} and {@link CardBalanceValidator} beans.
     *
     * @param encryptionService the encryption service used to encrypt and decrypt card numbers
     * @param balanceValidator the validator used to validate the card's balance
     */
    @Autowired
    public CardListener(EncryptionService encryptionService, CardBalanceValidator balanceValidator) {
        CardListener.encryptionService = encryptionService;
        CardListener.balanceValidator = balanceValidator;
    }

    /**
     * Called before persisting or updating a {@link Card} entity. This method ensures
     * that the card number is valid, encrypts the card number if necessary, and
     * validates the card's balance.
     *
     * @param card the card entity to be persisted or updated
     * @throws IllegalArgumentException if the card number is invalid
     */
    @PrePersist
    @PreUpdate
    public void beforeSaveOrUpdate(Card card) {
        if (card.getNumber() == null) {
            throw new IllegalArgumentException("Card number cannot be null");
        }

        if (!card.getNumber().startsWith("ENC:")) {
            validateCardNumber(card);
            encryptNumber(card);
        }

        balanceValidator.validate(card);
    }

    /**
     * Called after loading a {@link Card} entity from the database. This method
     * decrypts the card number if it is stored in an encrypted format.
     *
     * @param card the card entity that has been loaded from the database
     */
    @PostLoad
    public void decryptNumber(Card card) {
        if (card.getNumber() != null && card.getNumber().startsWith("ENC:")) {
            card.setNumber(encryptionService.decrypt(card.getNumber().substring(4)));
        }
    }

    /**
     * Encrypts the card number before persisting it in the database. The encrypted
     * number is prefixed with "ENC:" to mark it as encrypted.
     *
     * @param card the card entity whose number is to be encrypted
     */
    private void encryptNumber(Card card) {
        if (card.getNumber() != null) {
            card.setNumber("ENC:" + encryptionService.encrypt(card.getNumber()));
        }
    }

    /**
     * Validates the card number based on the card type.
     * This method checks whether the card number conforms to the expected format for the provided card type.
     * The validation includes length checks and prefix rules for different card types.
     *
     * @param card The card object containing the card type and card number to validate.
     * @throws IllegalArgumentException If the card type is null or if the card number does not match the expected pattern for the given card type.
     *                                 <ul>
     *                                     <li>For BANK_SPECIFIC type, the number must start with 2200 and have 16 digits.</li>
     *                                     <li>For VISA type, the number must start with 4 and have 16 digits.</li>
     *                                     <li>For MASTERCARD type, the number must start with 51-55 and have 16 digits.</li>
     *                                     <li>For AMERICAN_EXPRESS type, the number must start with 34 or 37 and have 15 digits.</li>
     *                                     <li>For other types, the number must be between 15 and 19 digits long.</li>
     *                                 </ul>
     *
     * @throws IllegalArgumentException If the card number does not match the expected pattern for the card type.
     */
    private void validateCardNumber(Card card) {
        CardType type = card.getType();
        String number = card.getNumber();

        if (type == null) {
            throw new IllegalArgumentException("Card type is required when number is provided");
        }

        switch (type) {
            case BANK_SPECIFIC:
                if (!number.matches("^2200\\d{12}$")) {
                    throw new IllegalArgumentException("Bank cards must start with 2200 and have 16 digits");
                }
                break;

            case VISA:
                if (!number.matches("^4\\d{15}$")) {
                    throw new IllegalArgumentException("VISA cards must start with 4 and have 16 digits");
                }
                break;

            case MASTERCARD:
                if (!number.matches("^5[1-5]\\d{14}$")) {
                    throw new IllegalArgumentException("MasterCard must start with 51-55 and have 16 digits");
                }
                break;

            case AMERICAN_EXPRESS:
                if (!number.matches("^3[47]\\d{13}$")) {
                    throw new IllegalArgumentException("American Express cards must start with 34 or 37 and have 15 digits");
                }
                break;

            default:
                if (!number.matches("^\\d{15,19}$")) {
                    throw new IllegalArgumentException("Card number must be 15 to 19 digits");
                }
        }
    }

}
