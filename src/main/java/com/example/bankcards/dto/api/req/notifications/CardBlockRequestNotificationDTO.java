package com.example.bankcards.dto.api.req.notifications;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.bankcards.util.Constants.CARD_CANNOT_BE_NULL;

/**
 * A data transfer object representing information about a user's card blocking request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardBlockRequestNotificationDTO implements NotificationReq {

    @NotNull(message = CARD_CANNOT_BE_NULL)
    private Long cardId;

    private String note;

}
