package com.example.bankcards.util.validators.impl;

import com.example.bankcards.util.validators.MinBalance;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
/**
 * Validator class for {@link MinBalance} annotation.
 * <p>
 * This validator checks that the annotated {@link java.math.BigDecimal} field
 * is greater than or equal to a configurable minimum value, which is injected
 * from application properties using Spring's {@link org.springframework.beans.factory.annotation.Value}.
 * </p>
 *
 * <p>
 * If the property {@code card.balance.min} is not found, the default value of 0.01 is used.
 * </p>
 *
 * <p>
 * Example property in application.properties:
 * <pre>
 *     card.balance.min=0.01
 * </pre>
 * </p>
 */

@Component
public class MinBalanceValidator implements ConstraintValidator<MinBalance, BigDecimal> {

    @Value("${spring.application.card.balance.min:0.01}")
    private String minBalanceStr;

    private BigDecimal minBalance;

    @PostConstruct
    public void init() {
        minBalance = new BigDecimal(minBalanceStr);
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && value.compareTo(minBalance) >= 0;
    }
}
