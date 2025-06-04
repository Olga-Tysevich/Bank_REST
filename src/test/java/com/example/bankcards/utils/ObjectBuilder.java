package com.example.bankcards.utils;

import com.example.bankcards.entity.*;
import com.example.bankcards.entity.enums.*;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.example.bankcards.utils.TestConstants.*;

@UtilityClass
public class ObjectBuilder {

    public static User buildAdmin() {
        return User.builder()
                .id(ADMIN_ID)
                .username(ADMIN_USERNAME)
                .name(ADMIN_NAME)
                .surname(ADMIN_SURNAME)
                .dateOfBirth(ADMIN_BIRTHDATE)
                .password(ADMIN_RAW_PASSWORD)
                .roleSet(Set.of(new Role(ROLE_ADMIN_ID, RoleEnum.ROLE_ADMIN)))
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
                .roleSet(Set.of(new Role(ROLE_USER_ID, RoleEnum.ROLE_USER)))
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
                .id(VISA_CARD_ID)
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
                .id(MASTERCARD_CARD_ID)
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
                .id(AMEX_CARD_ID)
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
                .id(BANK_SPECIFIC_CARD_ID)
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
        senderData.put(SourceOfFunds.SenderDataKey.ORGANIZATION_INN, "1234567890");
        senderData.put(SourceOfFunds.SenderDataKey.FULL_NAME, "Company Ltd");
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_TYPE, "Certificate");
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER, "123");
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY, "State");
        senderData.put(SourceOfFunds.SenderDataKey.ADDRESS, "Moscow");
        senderData.put(SourceOfFunds.SenderDataKey.PHONE, "1234567");
        senderData.put(SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE, "Charity");

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
        senderData.put(SourceOfFunds.SenderDataKey.FULL_NAME, "Ivan Ivanov");
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_TYPE, "Passport");
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_NUMBER, "123456");
        senderData.put(SourceOfFunds.SenderDataKey.DOCUMENT_ISSUED_BY, "Police");
        senderData.put(SourceOfFunds.SenderDataKey.PHONE, "7654321");
        senderData.put(SourceOfFunds.SenderDataKey.PAYMENT_PURPOSE, "Gift");

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
}