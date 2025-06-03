package com.example.bankcards.dto.api.req;

import com.example.bankcards.entity.enums.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.bankcards.util.Constants.*;

/**
 * A data transfer object representing information about the type of the new card and its owner.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCardDTO {

    @NotNull(message = CARD_TYPE_CODE_CANNOT_BE_NULL)
    @Schema(
            description = "Card type code. Must be one of the integer codes defined in the CardType enum. For example: 1 for MASTERCARD etc.",
            example = "1"
    )
    private CardType cardType;

    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long ownerId;

}
