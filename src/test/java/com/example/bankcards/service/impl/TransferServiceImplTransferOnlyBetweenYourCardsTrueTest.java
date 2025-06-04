package com.example.bankcards.service.impl;


import com.example.bankcards.BaseTest;
import com.example.bankcards.dto.api.req.MoneyTransferReqDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.events.processors.TransferQueueProcessor;
import com.example.bankcards.exception.ProhibitedException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bankcards.utils.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@TestPropertySource(properties = {
        "spring.application.card.transfer.betweenCards.permissions.onlyYourself=true"
})
public class TransferServiceImplTransferOnlyBetweenYourCardsTrueTest extends BaseTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransferQueueProcessor transferQueueProcessor;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferRepository transferRepository;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.schedule.timing.increaseBalance.initialDelay", () -> 1_000_000);
    }

    @Test
    public void testTransfer_successfully() {
        Card fromCard = cardRepository.findById(1L).orElseThrow();

        Card toCard = cardRepository.findById(2L).orElseThrow();

        fromCard.releaseFromHold(fromCard.getHold());
        fromCard.setBalance(new BigDecimal("200.00"));
        toCard.setBalance(BigDecimal.ZERO);

        fromCard.setStatus(CardStatus.ACTIVE);
        toCard.setStatus(CardStatus.ACTIVE);

        cardRepository.saveAllAndFlush(List.of(fromCard, toCard));

        BigDecimal initialTotal = fromCard.getBalance()
                .add(toCard.getBalance());

        MoneyTransferReqDTO transferRequest = new MoneyTransferReqDTO();
        transferRequest.setFromCardId(fromCard.getId());
        transferRequest.setToCardId(toCard.getId());
        transferRequest.setAmount(new BigDecimal("50.00"));

        super.setAuthentication(ADMIN_USERNAME, ADMIN_RAW_PASSWORD);
        Long transferId = transferService.createTransferRequest(transferRequest);

        fromCard = cardRepository.findById(1L).orElseThrow();
        assertThat(fromCard.getHold()).isEqualTo(new BigDecimal("50.00"));
        assertThat(fromCard.getBalance()).isEqualTo(new BigDecimal("150.00"));

        transferQueueProcessor.processQueue();

        fromCard = cardRepository.findById(1L).orElseThrow();
        toCard = cardRepository.findById(2L).orElseThrow();
        Transfer transferResult = transferRepository.findById(transferId).orElseThrow();

        assertThat(fromCard.getBalance())
                .isEqualTo(new BigDecimal("150.00"));
        assertThat(fromCard.getHold())
                .isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(toCard.getBalance())
                .isEqualTo(new BigDecimal("50.00"));

        BigDecimal finalTotal = fromCard.getBalance()
                .add(toCard.getBalance());
        assertThat(finalTotal)
                .isEqualByComparingTo(initialTotal);
        assertThat(transferResult.getStatus())
                .isEqualTo(TransferStatus.COMPLETED);
        assertThat(transferResult.getAmount())
                .isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(transferResult.getConfirmedAt())
                .isNotNull();
    }

    @Test
    public void testCreateTransfer_UnauthorizedUser_ThrowsUnauthorizedException() {
        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(1L);
        req.setToCardId(2L);
        req.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class);
    }

    @Test
    public void testCreateTransfer_AccountOwnedByAnotherUser_ThrowsProhibitedException() {
        super.setAuthentication(REGULAR_USERNAME, REGULAR_RAW_PASSWORD);

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(1L);
        req.setToCardId(2L);
        req.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("The card owner is different from the current user");
    }

    @Test
    public void testCreateTransfer_InsufficientBalance_ThrowsProhibitedException() {
        super.setAuthentication(ADMIN_USERNAME, ADMIN_RAW_PASSWORD);

        Card card = cardRepository.findById(1L).get();
        card.releaseFromHold(card.getHold());

        card.setBalance(new BigDecimal("5.00"));
        cardRepository.save(card);
        card = cardRepository.findById(1L).get();

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(card.getId());
        req.setToCardId(2L);
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    public void testCreateTransfer_ToAccountNotFound_ThrowsProhibitedException() {
        super.setAuthentication(REGULAR_USERNAME, REGULAR_RAW_PASSWORD);

        Card card = cardRepository.findById(3L).get();
        card.setBalance(new BigDecimal("100.00"));
        cardRepository.save(card);

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(card.getId());
        req.setToCardId(999L);
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("The specified recipient card does not exist!");
    }

    @Test
    public void testCreateTransfer_SameCard_ThrowsProhibitedException() {
        super.setAuthentication(REGULAR_USERNAME, REGULAR_RAW_PASSWORD);

        Card card = cardRepository.findById(3L).orElseThrow();
        card.setBalance(new BigDecimal("100.00"));
        cardRepository.save(card);

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(card.getId());
        req.setToCardId(card.getId());
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("It is not possible to transfer to the same card");
    }

    @Test
    public void testCreateTransfer_SenderCardLocked_ThrowsProhibitedException() {
        super.setAuthentication(ADMIN_USERNAME, ADMIN_RAW_PASSWORD);

        Card senderCard = cardRepository.findById(1L).orElseThrow();
        senderCard.setStatus(CardStatus.BLOCKED);
        senderCard.setBalance(new BigDecimal("100.00"));
        cardRepository.save(senderCard);

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(senderCard.getId());
        req.setToCardId(2L);
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("sender's card has expired or is blocked");
    }

    @Test
    public void testCreateTransfer_RecipientCardLocked_ThrowsProhibitedException() {
        super.setAuthentication(ADMIN_USERNAME, ADMIN_RAW_PASSWORD);

        Card recipientCard = cardRepository.findById(2L).orElseThrow();
        recipientCard.setStatus(CardStatus.EXPIRED);
        cardRepository.saveAndFlush(recipientCard);

        Card senderCard = cardRepository.findById(1L).orElseThrow();
        senderCard.setBalance(new BigDecimal("100.00"));
        cardRepository.saveAndFlush(senderCard);

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(senderCard.getId());
        req.setToCardId(recipientCard.getId());
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("recipient's card has expired or is blocked");
    }

    @Test
    public void testCreateTransfer_WhenTransferOnlyYourselfAllowed_TransferToAnotherUser_ThrowsProhibitedException() {

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

        BigDecimal senderInitialBalance = senderCard.getBalance();
        BigDecimal recipientInitialBalance = recipientCard.getBalance();

        MoneyTransferReqDTO req = new MoneyTransferReqDTO();
        req.setFromCardId(senderCard.getId());
        req.setToCardId(recipientCard.getId());
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransferRequest(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("only between your own cards");

        Card senderCardAfter = cardRepository.findById(senderCard.getId()).orElseThrow();
        Card recipientCardAfter = cardRepository.findById(recipientCard.getId()).orElseThrow();

        assertThat(senderCardAfter.getBalance()).isEqualByComparingTo(senderInitialBalance);
        assertThat(recipientCardAfter.getBalance()).isEqualByComparingTo(recipientInitialBalance);
    }

}