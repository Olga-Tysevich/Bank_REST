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

@Component
public class CardBalanceValidator {

    private final BigDecimal minBalance;

    public CardBalanceValidator(AppConf appConf) {
        this.minBalance = new BigDecimal(appConf.getMinBalanceStr());
    }

    public void validate(Card card) {
        if (card.getBalance() == null || card.getBalance().compareTo(minBalance) < 0) {
            throw new ConstraintViolationException("Invalid balance", createViolations(card));
        }
    }

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