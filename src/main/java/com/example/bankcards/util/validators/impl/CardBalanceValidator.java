package com.example.bankcards.util.validators.impl;

import com.example.bankcards.config.app.AppConf;
import com.example.bankcards.entity.Card;
import jakarta.validation.*;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

/**
 * Validator class responsible for ensuring that the balance of a {@link Card} is not below the minimum required balance.
 * This class checks if the balance of a card meets a predefined minimum value specified in the application configuration.
 *
 * <p>The minimum balance value is obtained from the application configuration ({@link AppConf}) during the initialization of the validator.
 * If the card's balance is invalid (i.e., null or lower than the minimum), a {@link ConstraintViolationException} is thrown.
 *
 * <p>This validator is typically used as a custom validation mechanism for card entities to ensure that the balance
 * is within acceptable limits before performing any transactions or processing.
 */
@Component
public class CardBalanceValidator {

    /** The minimum balance allowed for a card, obtained from the application configuration. */
    private final BigDecimal minBalance;

    /**
     * Constructor that initializes the validator with the minimum balance value from the application configuration.
     *
     * @param appConf The application configuration containing the minimum balance value.
     */
    public CardBalanceValidator(AppConf appConf) {
        this.minBalance = new BigDecimal(appConf.getMinBalanceStr());
    }

    /**
     * Validates the balance of the provided {@link Card}.
     * If the card's balance is null or lower than the minimum balance, a {@link ConstraintViolationException} is thrown.
     *
     * @param card The {@link Card} to validate.
     * @throws ConstraintViolationException If the card's balance is invalid (null or below the minimum balance).
     */
    public void validate(Card card) {
        if (card.getBalance() == null || card.getBalance().compareTo(minBalance) < 0) {
            throw new ConstraintViolationException("Invalid balance", createViolations(card));
        }
    }

    /**
     * Creates a set of {@link ConstraintViolation} instances to report validation errors.
     * This method generates a violation for the invalid balance field when the card balance is below the minimum value.
     *
     * @param card The {@link Card} that is being validated.
     * @return A set of constraint violations for the invalid card balance.
     */
    private Set<ConstraintViolation<Card>> createViolations(Card card) {
        return Collections.singleton(new ConstraintViolation<>() {
            @Override
            public String getMessage() {
                return "Balance must be at least " + minBalance;
            }

            @Override
            public Path getPropertyPath() {
                return PathImpl.createPathFromString("balance");
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public Card getRootBean() {
                return card;
            }

            @Override
            public Class<Card> getRootBeanClass() {
                return Card.class;
            }

            @Override
            public Object getLeafBean() {
                return card;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return card.getBalance();
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> type) {
                return null;
            }
        });
    }
}