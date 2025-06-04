package com.example.bankcards.service.impl;

import com.example.bankcards.config.app.AppConf;
import com.example.bankcards.config.app.DenyCancelTransfer;
import com.example.bankcards.dto.api.crypto.EncryptedCardNumber;
import com.example.bankcards.dto.api.req.MoneyTransferReqDTO;
import com.example.bankcards.dto.mappers.TransferMapper;
import com.example.bankcards.dto.redis.TransferMessageDTO;
import com.example.bankcards.entity.BackupAccount;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.SourceOfFunds;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.events.Events;
import com.example.bankcards.exception.ProhibitedException;
import com.example.bankcards.repository.BackupAccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.PrincipalExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.bankcards.entity.enums.SourceOfFunds.SenderDataKey.*;
import static com.example.bankcards.util.Constants.ENROLMENT_TRANSFER_DETAILS;

/**
 * Service implementation for handling bank card transfers.
 * This class handles the logic for creating, processing, and canceling transfers between user cards.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    /**
     * The application config bean
     *
     * @see AppConf
     */
    private final AppConf appConf;
    /**
     * The ApplicationEventPublisher bean
     *
     * @see ApplicationEventPublisher
     */
    private final ApplicationEventPublisher eventPublisher;
    /**
     * The TransferRepository bean
     *
     * @see TransferRepository
     */
    private final TransferRepository transferRepository;
    /**
     * The CardRepository bean
     *
     * @see CardRepository
     */
    private final CardRepository cardRepository;
    /**
     * The BackupAccountRepository bean
     *
     * @see BackupAccountRepository
     */
    private final BackupAccountRepository baRepository;
    /**
     * The TransferMapper bean
     *
     * @see TransferMapper
     */
    private final TransferMapper transferMapper;

    /**
     * Creates a new transfer request.
     *
     * @param transferReqDTO the transfer request data
     * @return the ID of the created transfer request
     * @throws ProhibitedException if the user is not authorized, if the card belongs to another user, or if any conditions prevent the transfer
     */
    @Override
    public Long createTransferRequest(@NotNull MoneyTransferReqDTO transferReqDTO) {
        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) {
            log.error("Unauthorized access attempt. No current user found.");
            throw new ProhibitedException("Unauthorized access");
        }

        Long fromCardId = transferReqDTO.getFromCardId();
        Long toCardId = transferReqDTO.getToCardId();

        EncryptedCardNumber fromCardNumber = transferReqDTO.getFromCardNumber();
        EncryptedCardNumber toCardNumber = transferReqDTO.getToCardNumber();

        if ((Objects.isNull(fromCardId) && Objects.isNull(fromCardNumber)) ||
                (Objects.isNull(toCardId) && Objects.isNull(toCardNumber))) {
            throw  new IllegalArgumentException("Invalid request id oe card number must be specified for both of card!");
        }

        fromCardId = Objects.requireNonNullElseGet(transferReqDTO.getFromCardId(),
                () -> cardRepository.findIdByEncryptedCardNumber(fromCardNumber.encrypted()).orElseThrow());

        toCardId = Objects.requireNonNullElseGet(transferReqDTO.getToCardId(),
                () -> cardRepository.findIdByEncryptedCardNumber(toCardNumber.encrypted()).orElseThrow());

        Long currentUserId = currentUser.getId();
        Long fromCardOwnerId = cardRepository.getOwnerIdById(fromCardId).orElseThrow();

        if (!fromCardOwnerId.equals(currentUserId)) {
            log.error("Prohibited action: Card owner id: {} does not match current user id: {}", fromCardOwnerId, currentUserId);

            throw new ProhibitedException("The card owner is different from the current user. " +
                    "Owner id: " + fromCardOwnerId + ", current user id: " + currentUserId);
        }

        if (fromCardId.equals(toCardId)) {
            log.error("It is not possible to transfer to the same card! Card id: {}", fromCardId);
            throw new ProhibitedException("It is not possible to transfer to the same card! Card id: " + fromCardId);
        }

        BigDecimal amount = transferReqDTO.getAmount();

        Optional<Card> fromCardOpt = cardRepository.findByIdAndSufficientBalance(fromCardId, amount);

        if (fromCardOpt.isEmpty()) {
            log.error("Insufficient balance for card id: {}. ", fromCardId);
            throw new ProhibitedException("Insufficient balance: the balance is too low for this operation. Card id: " + fromCardId);
        }

        Card fromCard = fromCardOpt.get();

        checkIfSenderCardIsLocked(fromCard.getStatus(), fromCardId);

        Optional<Card> toCardOpt = cardRepository.findById(toCardId);

        if (toCardOpt.isEmpty()) {
            log.error("Recipient card does not exist. Card id: {}", toCardId);
            throw new ProhibitedException("The specified recipient card does not exist!. Card id: " + toCardOpt);
        }

        Card toCard = toCardOpt.get();
        Long toCardOwnerId = toCard.getOwner().getId();

        checkIfTransferAvailableForYourselfOnly(fromCardOwnerId, toCardOwnerId, fromCardId, toCardId);

        checkIfRecipientCardIsLocked(toCard.getStatus(), toCardId);
        log.info("Creating transfer request from account id: {} by user id: {}", fromCardId, currentUserId);


        BigDecimal transferAmount = transferReqDTO.getAmount();
        BigDecimal currentBalance = fromCard.getBalance();

        if (currentBalance.compareTo(transferAmount) >= 0) {
            fromCard.addToHold(transferAmount);
        }

        cardRepository.save(fromCard);

        Transfer transfer = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(transferAmount)
                .status(TransferStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        transferRepository.save(transfer);

        TransferMessageDTO transferDTO = transferMapper.toTransferMessageDTO(transfer);

        Events.TransferEvent transferEvent = new Events.TransferEvent(transferDTO);

        eventPublisher.publishEvent(transferEvent);

        log.info("Transfer request successfully created for transfer id: {}", transfer.getId());

        return transfer.getId();
    }

    /**
     * Confirms and completes a transfer.
     * This method carries out the actual transfer operation from one card to another.
     *
     * @param transferDTO the transfer details including the amount, sender, and recipient card IDs
     * @throws ProhibitedException if the transfer is not allowed due to various reasons (e.g., insufficient balance, locked card)
     */
    @Override
    public void makeTransfer(@NotNull TransferMessageDTO transferDTO) {
        log.info("Attempting to make a transfer from card id {} to card id {}", transferDTO.getFromCardId(), transferDTO.getToCardId());
        try {
            Transfer transfer = carryOutTransfer(transferDTO);

            Events.TransferConfirmed transferConfirmed = new Events.TransferConfirmed(transferDTO);

            transferDTO.setStatus(transfer.getStatus());
            transferDTO.setConfirmedAt(transfer.getConfirmedAt());

            log.info("Transfer successfully confirmed for transfer id: {}", transferDTO.getId());
            eventPublisher.publishEvent(transferConfirmed);

        } catch (Exception e) {
            cancelTransfer(transferDTO);
            log.error("Transfer failed for transfer id: {}. Reversing operations.", transferDTO.getId(), e);
            eventPublisher.publishEvent(transferDTO);
        }
    }

    /**
     * Performs the actual transfer operation from one card to another.
     *
     * @param transferDTO the transfer details including the amount, sender, and recipient card IDs
     * @return the completed transfer
     * @throws ProhibitedException if any conditions are not met for the transfer
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Transfer carryOutTransfer(@NotNull TransferMessageDTO transferDTO) {
        Card toCard = cardRepository.findById(transferDTO.getToCardId()).orElseThrow();
        Long toCardOwnerId = toCard.getOwner().getId();

        checkIfRecipientCardIsLocked(toCard.getStatus(), toCard.getId());

        Card fromCard = cardRepository.findById(transferDTO.getFromCardId()).orElseThrow();
        Long fromCardOwnerId = fromCard.getOwner().getId();

        checkIfSenderCardIsLocked(fromCard.getStatus(), fromCard.getId());
        checkIfTransferAvailableForYourselfOnly(fromCardOwnerId, toCardOwnerId, fromCard.getId(), toCard.getId());

        BigDecimal transferAmount = transferDTO.getAmount();
        BigDecimal newToCardBalance = toCard.getBalance().add(transferAmount);

        toCard.setBalance(newToCardBalance);
        fromCard.releaseFromHold(transferAmount);

        cardRepository.saveAll(List.of(toCard, fromCard));

        transferDTO.setStatus(TransferStatus.COMPLETED);
        transferDTO.setConfirmedAt(LocalDateTime.now());

        Transfer transfer = transferRepository.findById(transferDTO.getId()).orElseThrow();
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setConfirmedAt(transferDTO.getConfirmedAt());

        return transferRepository.save(transfer);

    }

    /**
     * Cancels a transfer.
     * This method reverts the funds from the recipient card back to the sender's card and updates the transfer status.
     *
     * @param transferDTO the transfer details to be canceled
     * @throws ProhibitedException if the transfer cannot be canceled (e.g., due to locked cards)
     */
    @DenyCancelTransfer
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelTransfer(@NotNull TransferMessageDTO transferDTO) {

        Transfer transfer = transferMapper.toEntity(transferDTO);
        Card fromCard = cardRepository.findById(transferDTO.getFromCardId()).orElseThrow();

        transfer.setFromCard(fromCard);

        cancelTransfer(transfer);

    }

    /**
     * Cancels all pending transfers that were created today.
     * This method processes all transfers with a status of pending and reverts them.
     */
    @Override
    public void cancelPendingTransfers() {
        int page = 0;
        int size = 500;
        Page<Long> transferIds;

        do {
            transferIds = transferRepository.findTransferIdsWithStatusPendingAndCreatedAToToday(PageRequest.of(page, size));
            transferIds.forEach(transferId -> {
                try {
                    processSingleTransfer(transferId);
                } catch (Exception e) {
                    log.error("Error processing transfer cancel {}: {}", transferId, e.getMessage());
                }
            });
            page++;
        } while (transferIds.hasNext());
    }

    /**
     * Retries processing a single transfer.
     * If the transfer fails due to an optimistic locking exception, it will be retried.
     *
     * @param transferId the ID of the transfer to be processed
     */
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 100)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSingleTransfer(Long transferId) {
        transferRepository.findByIdForUpdate(transferId).ifPresent(transfer -> {

            transfer.setStatus(TransferStatus.FAILED);

            cancelTransfer(transfer);

        });
    }

    /**
     * Cancels a transfer and updates the database accordingly.
     * The sender's card balance is restored, and the transfer is marked as failed.
     *
     * @param transfer the transfer to be canceled
     */
    private void cancelTransfer(@NotNull Transfer transfer) {

        BigDecimal transferAmount = transfer.getAmount();

        Card fromCard = transfer.getFromCard();
        if (CardStatus.isLockedForTransfer(fromCard.getStatus())) {
            createBackupAccountIfCardIsBlock(transfer);
        }

        fromCard.setBalance(fromCard.getBalance().add(transferAmount));
        fromCard.releaseFromHold(transferAmount);

        cardRepository.save(fromCard);

        transfer.setStatus(TransferStatus.FAILED);

        transferRepository.save(transfer);
    }

    /**
     * Creates a backup account if the sender's card is locked.
     * This method saves a backup account for the sender if their card is blocked.
     *
     * @param transfer the transfer that triggered the backup account creation
     */
    private void createBackupAccountIfCardIsBlock(Transfer transfer) {
        User owner = transfer.getFromCard().getOwner();

        if (!baRepository.existsByOwner_Id(owner.getId())) {
            Long cardId = transfer.getFromCard().getId();
            BigDecimal amount = transfer.getAmount();

            Map<SourceOfFunds.SenderDataKey, String> senderData = new HashMap<>();
            SourceOfFunds transferSource = SourceOfFunds.BANK_CARD;

            senderData.putIfAbsent(FULL_NAME, owner.getName() + " " + owner.getSurname());
            senderData.putIfAbsent(DOCUMENT_TYPE, "Card with id: " + cardId);
            senderData.putIfAbsent(NOTE, String.format(ENROLMENT_TRANSFER_DETAILS, cardId));

            BackupAccount account = BackupAccount.builder()
                    .owner(owner)
                    .amount(amount)
                    .sourceOfFunds(transferSource)
                    .senderData(senderData)
                    .build();
            baRepository.save(account);
        } else {
            BackupAccount account = baRepository.findByOwner_Id(owner.getId());
            BigDecimal newAmount = account.getAmount().add(transfer.getAmount());
            account.setAmount(newAmount);
            baRepository.save(account);
        }
    }

    /**
     * Checks if the sender's card is locked for transfers.
     * If the card is locked, a {@link ProhibitedException} is thrown.
     *
     * @param status the current status of the sender's card
     * @param cardId the ID of the sender's card
     * @throws ProhibitedException if the card is locked
     */
    private void checkIfSenderCardIsLocked(CardStatus status, Long cardId) {
        if (CardStatus.isLockedForTransfer(status)) {
            log.error("The sender's card has expired or is blocked. Card id: {}", cardId);
            throw new ProhibitedException("The sender's card has expired or is blocked!. Card id: " + cardId);
        }
    }

    /**
     * Checks if the recipient's card is locked for transfers.
     * If the card is locked, a {@link ProhibitedException} is thrown.
     *
     * @param status the current status of the recipient's card
     * @param cardId the ID of the recipient's card
     * @throws ProhibitedException if the card is locked
     */
    private void checkIfRecipientCardIsLocked(CardStatus status, Long cardId) {
        if (CardStatus.isLockedForTransfer(status)) {
            log.error("The recipient's card has expired or is blocked. Card id: {}", cardId);
            throw new ProhibitedException("The recipient's card has expired or is blocked!. Card id: " + cardId);
        }
    }

    /**
     * Checks if the transfer is allowed between the sender and the recipient.
     * If transfers are restricted to only the sender's own cards, and the recipient's card does not belong to them, a {@link ProhibitedException} is thrown.
     *
     * @param fromCardOwnerId the owner ID of the sender's card
     * @param toCardOwnerId   the owner ID of the recipient's card
     * @param fromCardId      the ID of the sender's card
     * @param toCardId        the ID of the recipient's card
     * @throws ProhibitedException if the transfer is not allowed between the cards
     */
    private void checkIfTransferAvailableForYourselfOnly(Long fromCardOwnerId, Long toCardOwnerId,
                                                         Long fromCardId, Long toCardId) {
        boolean transferAllowedYourselfOnly = appConf.isTransferAllowedYourselfOnly();

        if (transferAllowedYourselfOnly && !fromCardOwnerId.equals(toCardOwnerId)) {
            log.error("Transfers are allowed only between your own cards. From card id: {}, to card id: {}", fromCardId, toCardId);
            throw new ProhibitedException("Transfers are allowed only between your own cards.!. From card id: " + fromCardId + ", to card id: " + toCardId);
        }
    }

}