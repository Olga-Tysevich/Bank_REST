package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.entity.listeners.CardListener;
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
@Table(name = "cards",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"card_type", "number"})
        })
@EntityListeners(CardListener.class)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cardIdSeq")
    @SequenceGenerator(name = "cardIdSeq", sequenceName = "card_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType type;

    @Column(name = "number", nullable = false)
    @NotNull(message = CARD_NUMBER_CANNOT_BE_NULL)
    private String number;

    @Column(name = "expiration", nullable = false)
    @NotNull(message = EXPIRATION_DATE_CANNOT_BE_NULL)
    private LocalDate expiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = CARD_STATUS_CANNOT_BE_NULL)
    private CardStatus status;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    @NotNull(message = BALANCE_CANNOT_BE_NULL)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = HOLD_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = HOLD_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal hold = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User owner;

    @Column(name = "is_deleted", nullable = false)
    @NotNull(message = IS_DELETED_CANNOT_BE_NULL)
    @Builder.Default
    private Boolean isDeleted = false;

    @Version
    private Long version;

    public void addToHold(@NotNull(message = TRANSFER_AMOUNT_CANNOT_BE_NUL)
            @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_MUST_BE_POSITIVE) BigDecimal amount)
    {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available balance to hold funds.");
        }
        this.hold = this.hold.add(amount);
        this.balance = this.balance.subtract(amount);
    }

    public void releaseFromHold(@NotNull(message = TRANSFER_AMOUNT_CANNOT_BE_NUL)
                                @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_MUST_BE_POSITIVE) BigDecimal amount)
    {
        if (getHold().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cannot release more than is held.");
        }
        this.hold = this.hold.subtract(amount);
    }

}
