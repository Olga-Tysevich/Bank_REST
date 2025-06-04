package com.example.bankcards.service.impl;

import com.example.bankcards.BaseTest;
import com.example.bankcards.dto.api.req.MoneyTransferReqDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.events.processors.TransferQueueProcessor;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bankcards.utils.TestConstants.ADMIN_RAW_PASSWORD;
import static com.example.bankcards.utils.TestConstants.ADMIN_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
        "spring.application.card.transfer.betweenCards.permissions.onlyYourself=false"
})
class TransferServiceImplTransferOnlyBetweenYourCardsFalseTest extends BaseTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransferService transferService;


    @Autowired
    private TransferQueueProcessor transferQueueProcessor;

    @Test
    public void testCreateTransfer_WhenTransferBetweenDifferentUsersAllowed_TransferSucceedsAndBalancesUpdated() {

        super.setAuthentication(ADMIN_USERNAME, ADMIN_RAW_PASSWORD);

        Card senderCard = cardRepository.findById(1L).orElseThrow();
        Card recipientCard = cardRepository.findById(3L).orElseThrow();

        assertThat(senderCard.getOwner().getId())
                .isNotEqualTo(recipientCard.getOwner().getId());

        senderCard.setBalance(new BigDecimal("100.00"));
        recipientCard.setBalance(new BigDecimal("50.00"));

        senderCard.setStatus(CardStatus.ACTIVE);
        recipientCard.setStatus(CardStatus.ACTIVE);

        cardRepository.saveAllAndFlush(List.of(senderCard, recipientCard));

        BigDecimal transferAmount = new BigDecimal("10.00");
        BigDecimal expectedSenderBalance = senderCard.getBalance().subtract(transferAmount);
        BigDecimal expectedRecipientBalance = recipientCard.getBalance().add(transferAmount);

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(senderCard.getId());
        req.setToCardId(recipientCard.getId());
        req.setAmount(transferAmount);

        transferService.createTransferRequest(req);
        transferQueueProcessor.processQueue();

        Card senderCardAfter = cardRepository.findById(senderCard.getId()).orElseThrow();
        Card recipientCardAfter = cardRepository.findById(recipientCard.getId()).orElseThrow();

        assertThat(senderCardAfter.getBalance()).isEqualByComparingTo(expectedSenderBalance);
        assertThat(recipientCardAfter.getBalance()).isEqualByComparingTo(expectedRecipientBalance);
    }
}
