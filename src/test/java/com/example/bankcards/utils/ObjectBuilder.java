package com.example.bankcards.utils;

import com.example.bankcards.dto.api.req.EnrollDTO;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.entity.*;
import com.example.bankcards.entity.enums.*;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.example.bankcards.utils.TestConstants.*;

@UtilityClass
public class ObjectBuilder {

    public UpdateCardDTO buildUpdateCardDTOWithRegularAmex() {
        return UpdateCardDTO.builder()
                .cardId(AMEX_CARD_ID_OWNER_REGULAR)
                .enrollment(buildEnrollDTO())
                .build();
    }

    public EnrollDTO buildEnrollDTO() {
        return EnrollDTO.builder()
                .cardId(AMEX_CARD_ID_OWNER_REGULAR)
                .senderType(SenderType.INDIVIDUAL)
                .sourceOfFunds(SourceOfFunds.CASH_CONTRIBUTED_BY_INDIVIDUAL)
                .enrollmentInformation(buildSenderData())
                .amount(new BigDecimal("100.00"))
                .build();
    }

    public Map<SourceOfFunds.SenderDataKey, String> buildSenderData() {
        Map<SourceOfFunds.SenderDataKey, String> senderData = new EnumMap<>(SourceOfFunds.SenderDataKey.class);
        senderData.put(SourceOfFunds.SenderDataKey.FULL_NAME, SENDER_FULL_NAME);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_TYPE, SENDER_DOCUMENT_TYPE);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER, SENDER_DOCUMENT_NUMBER);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY, SENDER_DOCUMENT_ISSUED_BY);
        senderData.put(SourceOfFunds.SenderDataKey.PHONE, SENDER_PHONE);
        senderData.put(SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE, SENDER_PAYMENT_PURPOSE);
        return senderData;
    }

    public static User buildAdmin() {
        return User.builder()
                .id(ADMIN_ID)
                .username(ADMIN_USERNAME)
                .name(ADMIN_NAME)
                .surname(ADMIN_SURNAME)
                .dateOfBirth(ADMIN_BIRTHDATE)
                .password(ADMIN_RAW_PASSWORD)
                .roleSet(Set.of(buildAdminRole()))
                .build();
    }

    public static User buildRegularUser() {
        return User.builder()
                .id(REGULAR_USER_ID)
                .username(REGULAR_USERNAME)
                .name(REGULAR_NAME)
                .surname(REGULAR_SURNAME)
                .dateOfBirth(REGULAR_BIRTHDATE)
                .password(REGULAR_RAW_PASSWORD)
                .roleSet(Set.of(buildUserRole()))
                .build();
    }

    public static Role buildUserRole() {
        return new Role(ROLE_USER_ID, RoleEnum.ROLE_USER);
    }

    public static Role buildAdminRole() {
        return new Role(ROLE_ADMIN_ID, RoleEnum.ROLE_ADMIN);
    }

    public static Card buildAdminVisaCard() {
        return Card.builder()
                .id(VISA_CARD_ID_OWNER_ADMIN)
                .type(CardType.VISA)
                .number(VISA_CARD_NUMBER_ENCODED)
                .expiration(VISA_EXPIRATION)
                .status(CardStatus.ACTIVE)
                .balance(VISA_BALANCE)
                .hold(BigDecimal.ZERO)
                .owner(buildAdmin())
                .isDeleted(false)
                .build();
    }

    public static Card buildAdminMastercardCard() {
        return Card.builder()
                .id(MASTERCARD_CARD_ID_OWNER_ADMIN)
                .type(CardType.MASTERCARD)
                .number(MASTERCARD_CARD_NUMBER_ENCODED)
                .expiration(MASTERCARD_EXPIRATION)
                .status(CardStatus.BLOCKED)
                .balance(MASTERCARD_BALANCE)
                .hold(BigDecimal.ZERO)
                .owner(buildAdmin())
                .isDeleted(false)
                .build();
    }

    public static Card buildUserAmexCard() {
        return Card.builder()
                .id(AMEX_CARD_ID_OWNER_REGULAR)
                .type(CardType.AMERICAN_EXPRESS)
                .number(AMEX_CARD_NUMBER_ENCODED)
                .expiration(AMEX_EXPIRATION)
                .status(CardStatus.ACTIVE)
                .balance(AMEX_BALANCE)
                .hold(BigDecimal.ZERO)
                .owner(buildRegularUser())
                .isDeleted(false)
                .build();
    }

    public static Card buildUserBankSpecificCard() {
        return Card.builder()
                .id(BANK_SPECIFIC_CARD_ID_OWNER_REGULAR)
                .type(CardType.BANK_SPECIFIC)
                .number(BANK_SPECIFIC_CARD_NUMBER_ENCODED)
                .expiration(BANK_SPECIFIC_EXPIRATION)
                .status(CardStatus.EXPIRED)
                .balance(BANK_SPECIFIC_BALANCE)
                .hold(BigDecimal.ZERO)
                .owner(buildRegularUser())
                .isDeleted(false)
                .build();
    }

    public static Transfer buildCompletedTransfer() {
        return Transfer.builder()
                .id(TRANSFER_1_ID)
                .fromCard(buildAdminVisaCard())
                .toCard(buildUserAmexCard())
                .amount(TRANSFER_1_AMOUNT)
                .status(TransferStatus.COMPLETED)
                .createdAt(TRANSFER_1_CREATED)
                .confirmedAt(TRANSFER_1_CONFIRMED)
                .build();
    }

    public static Transfer buildPendingTransfer() {
        return Transfer.builder()
                .id(TRANSFER_2_ID)
                .fromCard(buildUserAmexCard())
                .toCard(buildAdminVisaCard())
                .amount(TRANSFER_2_AMOUNT)
                .status(TransferStatus.PENDING)
                .createdAt(TRANSFER_2_CREATED)
                .build();
    }

    public static BackupAccount buildAdminBackupAccount() {
        Map<SourceOfFunds.SenderDataKey, String> senderData = new EnumMap<>(SourceOfFunds.SenderDataKey.class);
        senderData.put(SourceOfFunds.SenderDataKey.ORGANIZATION_INN, ORG_INN);
        senderData.put(SourceOfFunds.SenderDataKey.FULL_NAME, ORG_NAME);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_TYPE, ORG_DOCUMENT_TYPE);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER, ORG_DOCUMENT_NUMBER);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY, ORG_DOCUMENT_ISSUED_BY);
        senderData.put(SourceOfFunds.SenderDataKey.ADDRESS, ORG_ADDRESS);
        senderData.put(SourceOfFunds.SenderDataKey.PHONE, ORG_PHONE);
        senderData.put(SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE, ORG_PURPOSE);

        return BackupAccount.builder()
                .id(BACKUP_ACCOUNT_1_ID)
                .sourceOfFunds(SourceOfFunds.CASH_CONTRIBUTED_BY_ORGANIZATION)
                .senderData(senderData)
                .owner(buildAdmin())
                .amount(BACKUP_ACCOUNT_1_AMOUNT)
                .build();
    }

    public static BackupAccount buildUserBackupAccount() {
        Map<SourceOfFunds.SenderDataKey, String> senderData = new EnumMap<>(SourceOfFunds.SenderDataKey.class);
        senderData.put(SourceOfFunds.SenderDataKey.FULL_NAME, INDIVIDUAL_NAME);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_TYPE, INDIVIDUAL_DOCUMENT_TYPE);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER, INDIVIDUAL_DOCUMENT_NUMBER);
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY, INDIVIDUAL_DOCUMENT_ISSUED_BY);
        senderData.put(SourceOfFunds.SenderDataKey.PHONE, INDIVIDUAL_PHONE);
        senderData.put(SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE, INDIVIDUAL_PURPOSE);

        return BackupAccount.builder()
                .id(BACKUP_ACCOUNT_2_ID)
                .sourceOfFunds(SourceOfFunds.CASH_CONTRIBUTED_BY_INDIVIDUAL)
                .senderData(senderData)
                .owner(buildRegularUser())
                .amount(BACKUP_ACCOUNT_2_AMOUNT)
                .build();
    }

    public static RefreshToken buildAdminRefreshToken() {
        return new RefreshToken(ADMIN_USERNAME, ADMIN_REFRESH_TOKEN);
    }

    public static RefreshToken buildUserRefreshToken() {
        return new RefreshToken(REGULAR_USERNAME, USER_REFRESH_TOKEN);
    }

    public static Role buildAdminUserRole() {
        return new Role(ROLE_ADMIN_ID, RoleEnum.ROLE_ADMIN);
    }

    public static Role buildRegularUserRole() {
        return new Role(ROLE_USER_ID, RoleEnum.ROLE_USER);
    }

    public static User buildCustomUser(String username, String password, String name, String surname) {
        return User.builder()
                .username(username)
                .password(password)
                .name(name)
                .surname(surname)
                .roleSet(Set.of(buildUserRole(), buildAdminRole()))
                .dateOfBirth(LocalDate.now().minusYears(30))
                .build();
    }

    public static Card buildCustomCard(User owner, CardType type, String number, int expYears, int expMonths) {
        return Card.builder()
                .owner(owner)
                .type(type)
                .number(number)
                .expiration(LocalDate.now().plusYears(expYears).plusMonths(expMonths))
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .isDeleted(false)
                .build();
    }

}
