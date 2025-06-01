package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.example.bankcards.util.Constants.*;

/**
 * This class represents a Transfer entity with its attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transferIdSeq")
    @SequenceGenerator(name = "transferIdSeq", sequenceName = "transfer_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_card_id")
    @NotNull(message = CARD_FROM_CANNOT_BE_NULL)
    private Card fromCard;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_card_id")
    @NotNull(message = CARD_TO_CANNOT_BE_NULL)
    private Card toCard;

    @Column(name = "amount", precision = 19, scale = 2)
    @NotNull(message = TRANSFER_AMOUNT_BE_POSITIVE)
    @DecimalMin(value = "0.01", message = TRANSFER_AMOUNT_BE_POSITIVE)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "created_at",nullable = false)
    @NotNull(message = TRANSFER_DATE_CANNOT_BE_NUL)
    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    @PastOrPresent(message = TRANSFER_CONFIRM_DATE_MUST_BE_IN_PAST)
    private LocalDateTime confirmedAt;

    @Version
    private Long version;

}