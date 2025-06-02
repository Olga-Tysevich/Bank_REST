package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.listeners.CardListener;
import com.example.bankcards.util.validators.MinBalance;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.bankcards.util.Constants.*;

/**
 * This class represents a Card entity with its attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cards")
@EntityListeners(CardListener.class)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cardIdSeq")
    @SequenceGenerator(name = "cardIdSeq", sequenceName = "card_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @Column(name = "number", nullable = false)
    @NotNull(message = CARD_NUMBER_CANNOT_BE_NULL)
    @Pattern(regexp = CARD_NUMBER_REGEX, message = INVALID_CARD_NUMBER_MESSAGE)
    private String number;

    @Column(name = "expiration", nullable = false)
    @NotNull(message = EXPIRATION_DATE_CANNOT_BE_NULL)
    private LocalDate expiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    @NotNull(message = BALANCE_MUST_BE_POSITIVE)
    @MinBalance
    private BigDecimal balance;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = HOLD_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = HOLD_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal hold = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User owner;

    @Version
    private Long version;

    public void addToHold(@NotNull(message = TRANSFER_AMOUNT_CANNOT_BE_NUL)
            @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE) BigDecimal amount)
    {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available balance to hold funds.");
        }
        this.hold = this.hold.add(amount);
    }

    public void releaseFromHold(@NotNull(message = TRANSFER_AMOUNT_CANNOT_BE_NUL)
                                @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE) BigDecimal amount)
    {
        if (getHold().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cannot release more than is held.");
        }
        this.hold = this.hold.subtract(amount);
    }

}
