package com.example.bankcards.dto.redis;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.bankcards.util.Constants.*;

/**
 * Data transfer object representing Transfer information for redis queue.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardBlockRequestCreatedMessageDTO {

    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @NotNull(message = CREATED_AT_DATE_CANNOT_BE_NULL)
    private LocalDateTime createdAt;

    @NotNull(message = USER_CANNOT_BE_NULL)
    private Long userId;

    @NotNull(message = CARD_CANNOT_BE_NULL)
    private Long cardId;

    private String note = "";

}
