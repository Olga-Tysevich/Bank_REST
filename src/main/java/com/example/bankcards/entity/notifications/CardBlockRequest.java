package com.example.bankcards.entity.notifications;

import com.example.bankcards.entity.Card;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.example.bankcards.util.Constants.*;

/**
 * This abstract class represents CardBlockRequest notifications intended for processing by the admin.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "card_block_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CardBlockRequest extends AdminNotification {

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_card_id")
    @NotNull(message = CARD_CANNOT_BE_NULL)
    private Card card;


    @Column(name = "is_confirmed", nullable = false)
    @NotNull(message = IS_CONFIRMED_CANNOT_BE_NULL)
    @Builder.Default
    private Boolean isConfirmed = false;

}

