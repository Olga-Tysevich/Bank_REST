package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.util.validators.MinBalance;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "card")
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

}
