package com.example.bankcards.entity.enums;

import lombok.Getter;

import java.util.List;

/**
 * Enum representing different sources of funds for a transfer. This enum is primarily used for generating random
 * transfers in special cases such as testing, simulations, or handling edge-case scenarios in financial transactions.
 * It defines a set of predefined categories for the source of funds, which may include bank cards, cash contributions
 * from organizations or individuals, and other related sources.
 * <p>
 * Each source type can have its own set of associated sender data, represented by the nested {@link SenderDataKey} enum.
 * This ensures that the correct set of parameters, such as personal or organizational information, is expected
 * for each transfer type.
 * <p>
 * The {@link SourceOfFunds} enum helps in the systematic generation of expected transfer data, especially useful in
 * cases where random or test transfers need to be created with specific sender attributes.
 *
 * @see SenderDataKey
 */
@Getter
public enum SourceOfFunds {

    /**
     * Transfer from a regular bank card.
     */
    BANK_CARD,

    /**
     * Transfer from an external bank card (not from the same bank).
     */
    EXTERNAL_BANK_CARD,

    /**
     * Cash contributed by an organization for the transfer.
     */
    CASH_CONTRIBUTED_BY_ORGANIZATION,

    /**
     * Cash contributed by an individual for the transfer.
     */
    CASH_CONTRIBUTED_BY_INDIVIDUAL;

    /**
     * Enum representing keys for the sender's data associated with a particular source of funds.
     * This nested enum defines the set of expected sender attributes for each source of funds type.
     * These keys are used to ensure that the necessary sender data (such as personal or organizational
     * details) is provided when creating or processing a transfer.
     *
     * This enum is particularly useful when generating random or special-case transfers in scenarios
     * such as testing, edge-case handling, or simulating different types of financial transactions.
     *
     * @see SourceOfFunds
     */
    @Getter
    public enum SenderDataKey {

        /**
         * Full name of the sender.
         */
        FULL_NAME,

        /**
         * Type of the sender's identification document (e.g., passport, ID card).
         */
        DOCUMENT_TYPE,

        /**
         * Identification document number.
         */
        DOCUMENT_NUMBER,

        /**
         * Issue date of the identification document.
         */
        DOCUMENT_ISSUE_DATE,

        /**
         * The entity that issued the identification document.
         */
        DOCUMENT_ISSUED_BY,

        /**
         * Address of the sender.
         */
        ADDRESS,

        /**
         * Phone number of the sender.
         */
        PHONE,

        /**
         * The short name of the organization (if applicable).
         */
        ORGANIZATION_SHORT_NAME,

        /**
         * Tax Identification Number (INN) of the organization (if applicable).
         */
        ORGANIZATION_INN,

        /**
         * Purpose of the payment.
         */
        PAYMENT_PURPOSE,

        /**
         * Additional notes regarding the sender or the transfer.
         */
        NOTE;

        public static List<SenderDataKey> mandatoryForIndividual() {
            return List.of(
                    SourceOfFunds.SenderDataKey.FULL_NAME,
                    SourceOfFunds.SenderDataKey.DOCUMENT_TYPE,
                    SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER,
                    SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY,
                    SourceOfFunds.SenderDataKey.PHONE,
                    SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE
            );
        }

        public static List<SenderDataKey> mandatoryForCompany() {
            return List.of(
                    SourceOfFunds.SenderDataKey.ORGANIZATION_INN,
                    SourceOfFunds.SenderDataKey.FULL_NAME,
                    SourceOfFunds.SenderDataKey.DOCUMENT_TYPE,
                    SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER,
                    SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY,
                    SourceOfFunds.SenderDataKey.ADDRESS,
                    SourceOfFunds.SenderDataKey.PHONE,
                    SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE
            );
        }
    }
}
