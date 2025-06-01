package com.example.bankcards.dto.redis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.bankcards.util.Constants.*;

/**
 * Data transfer object representing Transfer information for redis queue.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMessage implements Serializable {

    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @NotNull(message = CARD_FROM_CANNOT_BE_NULL)
    private Long fromCardId;

    @NotNull(message = CARD_TO_CANNOT_BE_NULL)
    private Long toCardId;

    @NotNull(message = TRANSFER_AMOUNT_BE_POSITIVE)
    @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE)
    private BigDecimal amount;

    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime createdAt;

    @NotNull(message = TRANSFER_DATE_CANNOT_BE_NUL)
    @PastOrPresent(message = TRANSFER_DATE_MUST_BE_IN_PAST)
    private LocalDateTime confirmedAt;

    @NotNull(message = TRANSFER_STATUS_CANNOT_BE_NUL)
    private TransferStatus status;

    @NotNull(message = VERSION_CANNOT_BE_NULL)
    @Builder.Default
    private Long version = 0L;
}