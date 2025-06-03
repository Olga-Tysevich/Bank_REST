package com.example.bankcards.dto.api.req;

import com.example.bankcards.entity.enums.SenderType;
import com.example.bankcards.entity.enums.SourceOfFunds;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import static com.example.bankcards.util.Constants.*;

/**
 * A data transfer object that represents information about enroll for card.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollDTO {
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long cardId;
    /**
     * @see SenderType
     */
    @NotNull(message = SENDER_TYPE_CANNOT_BE_NULL)
    private SenderType senderType;

    /**
     * @see SourceOfFunds
     */
    @NotNull(message = SOURCE_OF_FUNDS_CANNOT_BE_NULL)
    private SourceOfFunds sourceOfFunds;


    Map<SourceOfFunds.SenderDataKey, String> enrollmentInformation;

    @NotNull(message = AMOUNT_CANNOT_BE_NULL)
    @DecimalMin(value = "0.00", message = AMOUNT_MUST_BE_POSITIVE)
    private BigDecimal amount;

}
