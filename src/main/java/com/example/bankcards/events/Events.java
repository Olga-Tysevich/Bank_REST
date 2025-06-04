package com.example.bankcards.events;


import com.example.bankcards.dto.redis.CardBlockRequestCreatedMessageDTO;
import com.example.bankcards.dto.redis.TransferMessageDTO;

/**
 * Abstract class representing various events in the bank transfer process.
 * <p>
 * This class contains different types of events related to bank transfers. Each event encapsulates data in the form of
 * {@link TransferMessageDTO}, which carries the details of the transfer.
 * </p>
 */
public abstract class Events {
    /**
     * Event triggered when a transfer occurs.
     * <p>
     * This event carries the transfer details in the form of a {@link TransferMessageDTO} object.
     * It is typically used to notify systems that a new transfer needs to be processed.
     * </p>
     *
     * @param transferDTO the details of the transfer to be processed.
     * @see TransferMessageDTO
     */
    public record TransferEvent(TransferMessageDTO transferDTO) {
    }

    /**
     * Event triggered when a transfer is confirmed.
     * <p>
     * This event carries the details of a successfully confirmed transfer via a {@link TransferMessageDTO} object.
     * It is typically used to notify systems that a transfer has been successfully processed and confirmed.
     * </p>
     *
     * @param transferDTO the details of the confirmed transfer.
     * @see TransferMessageDTO
     */
    public record TransferConfirmed(TransferMessageDTO transferDTO) {
    }

    /**
     * Domain event representing the creation of a card block request.
     * <p>
     * This event wraps a {@link CardBlockRequestCreatedMessageDTO} object
     * and is typically published when a new card block request is initiated.
     * </p>
     *
     * @param cardBlockRequestDTO the DTO containing details of the card block request
     */
    public record CardBlockRequestCreated(CardBlockRequestCreatedMessageDTO cardBlockRequestDTO) {

    }

}