package com.example.bankcards.dto.api.resp;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.util.validators.MinBalance;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.bankcards.util.Constants.*;

/**
 * Data transfer object representing Card information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDTO {

    private Long id;

    /**
     * @see CardType
     */
    private int cardTypeCode;

    @NotNull(message = CARD_NUMBER_CANNOT_BE_NULL)
    @Pattern(regexp = CardType.MASKED_CARD_PATTERN, message = INVALID_CARD_MASK_MESSAGE)
    private String numberMask;

    @NotNull(message = EXPIRATION_DATE_CANNOT_BE_NULL)
    private LocalDate expiration;

    /**
     * @see CardStatus
     */
    @NotNull(message = CARD_STATUS_CANNOT_BE_NULL)
    private CardStatus status;

    @NotNull(message = BALANCE_CANNOT_BE_NULL)
    @MinBalance
    private BigDecimal balance;

    @NotNull(message = HOLD_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = HOLD_MUST_BE_POSITIVE)
    @Builder.Default
    private BigDecimal hold = BigDecimal.ZERO;

    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long ownerId;

}
