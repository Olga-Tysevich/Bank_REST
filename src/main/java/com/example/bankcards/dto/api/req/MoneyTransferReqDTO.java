package com.example.bankcards.dto.api.req;

import com.example.bankcards.dto.api.crypto.EncryptedCardNumber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.example.bankcards.util.Constants.*;

/**
 * Data transfer object representing Money Transfer information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransferReqDTO {

    @NotNull(message = CARD_FROM_CANNOT_BE_NULL)
    private Long fromCardId;

    @NotNull(message = CARD_TO_CANNOT_BE_NULL)
    private Long toCardId;

    /**
     * @see EncryptedCardNumber
     */
    private EncryptedCardNumber fromCardNumber;

    /**
     * @see EncryptedCardNumber
     */
    private EncryptedCardNumber toCardNumber;

    @NotNull(message = TRANSFER_AMOUNT_CANNOT_BE_NUL)
    @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE)
    private BigDecimal amount;

    @Override
    public String toString() {
        return "MoneyTransferReqDTO{" +
                "fromCardId=" + fromCardId +
                ", toCardId=" + toCardId +
                ", fromCardNumber=" + fromCardNumber.cardMask() +
                ", toCardNumber=" + toCardNumber.cardMask() +
                ", amount=" + amount +
                '}';
    }
}
