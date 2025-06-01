package com.example.bankcards.util.validators;

import com.example.bankcards.util.validators.impl.MinBalanceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating that a BigDecimal value is greater than or equal
 * to the minimum card balance specified in the application properties.
 * <p>
 * This constraint should be used on fields of type {@link java.math.BigDecimal}.
 * <p>
 * The minimum balance value is injected via Spring's @Value annotation inside
 * the corresponding validator.
 * <p>
 * Example usage:
 * <pre>
 *     @MinBalance
 *     private BigDecimal balance;
 * </pre>
 * <p>
 * The default minimum balance value can be configured using:
 * <pre>
 *     card.balance.min=0.01
 * </pre>
 * in application.properties or application.yml.
 */

@Constraint(validatedBy = MinBalanceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinBalance {
    String message() default "Balance below the established minimum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
