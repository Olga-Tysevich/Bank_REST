package com.example.bankcards.service;

import com.example.bankcards.dto.api.req.MoneyTransferReqDTO;
import com.example.bankcards.dto.redis.TransferMessageDTO;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code TransferService} interface defines the operations related to money transfers.
 * Implementations of this interface will provide the actual logic for initiating, processing, and canceling transfers.
 */
public interface TransferService {
    /**
     * Initiates a transfer based on the provided transfer details.
     *
     * @param transferDTO The transfer details encapsulated in a {@link TransferMessageDTO} object.
     * @throws IllegalArgumentException if the transfer fails or is invalid.
     */
    void makeTransfer(@NotNull TransferMessageDTO transferDTO);

    /**
     * Creates a transfer request based on the provided money transfer request details.
     *
     * @param moneyTransferReq The money transfer request details encapsulated in a {@link MoneyTransferReqDTO} object.
     * @return The ID of the created transfer request.
     * @throws IllegalArgumentException if the transfer request is invalid or cannot be created.
     */
    Long createTransferRequest(@NotNull MoneyTransferReqDTO moneyTransferReq);

    /**
     * Cancels any pending transfers in the system.
     * This operation may be useful in situations like canceling unprocessed transactions
     * or resetting pending transfers due to errors or timeouts.
     *
     * @throws IllegalStateException if there is an issue canceling the pending transfers.
     */
    void cancelPendingTransfers();

}
