package com.example.bankcards.dto.api.req;

import com.example.bankcards.dto.api.crypto.EncryptedCardNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.bankcards.util.Constants.*;

/**
 * A data transfer object that represents information about the data being updated in an existing map.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCardDTO {
    private Long cardId;

    private EncryptedCardNumber cardNumber;

    /**
     * @see EnrollDTO
     */
    @NotNull(message = ENROLLMENT_CANNOT_BE_NULL)
    private EnrollDTO enrollment;
}
