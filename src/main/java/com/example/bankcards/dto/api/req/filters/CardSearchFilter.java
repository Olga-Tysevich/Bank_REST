package com.example.bankcards.dto.api.req.filters;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Data transfer object representing filter for card search.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSearchFilter implements SearchFilter {

    private Set<Long> idSet;

    private Set<CardType> cardTypes;

    private Set<String> number;

    private LocalDate expirationFrom;

    private LocalDate expirationTo;

    private Set<CardStatus> status;

    private BigDecimal balanceFrom;

    private BigDecimal balanceTo;

    private BigDecimal holdFrom;

    private BigDecimal holdTo;

    private Set<Long> ownerIdSet;

    private Boolean isDeleted;

}
