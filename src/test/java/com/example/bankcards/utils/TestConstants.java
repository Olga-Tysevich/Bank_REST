package com.example.bankcards.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TestConstants {
    String SCHEME_SOURCE_PATH = "schemas/";
    String BASE_URL = "http://localhost:%s";
    int DEFAULT_APP_PORT = 8080;
    long DEFAULT_TIMEOUT = 1500L;
    long DEFAULT_PAGE = 0;
    String COUNT_PER_PAGE_PARAM = "countPerPage";
    long DEFAULT_COUNT_PER_PAGE = 5;
    String PAGE_PARAM = "pageNum";
    String USER_ID_PARAM = "id";

    // Пользователи
    String ADMIN_USERNAME = "admin@bank.com";
    String ADMIN_NAME = "Admin";
    String ADMIN_SURNAME = "Adminov";
    LocalDate ADMIN_BIRTHDATE = LocalDate.of(1980, 1, 15);
    String ADMIN_RAW_PASSWORD = "password";

    String REGULAR_USERNAME = "user@bank.com";
    String REGULAR_NAME = "Ivan";
    String REGULAR_SURNAME = "Ivanov";
    LocalDate REGULAR_BIRTHDATE = LocalDate.of(1990, 5, 20);
    String REGULAR_RAW_PASSWORD = "password";

    // BCrypt-закодированные пароли (для raw password = "password")
    String ENCODED_PASSWORD = "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6";

    // Карты
    String VISA_CARD_NUMBER = "4111111111111111";
    String VISA_CARD_NUMBER_ENCODED = "ENC:7bK/2TnT/t14EvJ54wuadgbb0lJO2pSgRxctIDarhYM=";
    String MASTERCARD_CARD_NUMBER = "5111111111111111";
    String MASTERCARD_CARD_NUMBER_ENCODED = "ENC:aYPwO036kxaaWORShhfwAgbb0lJO2pSgRxctIDarhYM=";
    String AMEX_CARD_NUMBER = "371111111111111";
    String AMEX_CARD_NUMBER_ENCODED = "ENC:NZ24g+T5b2GgUW1gKOrZ4Q==";
    String BANK_SPECIFIC_CARD_NUMBER = "2200111122223333";
    String BANK_SPECIFIC_CARD_NUMBER_ENCODED = "ENC:7uCjUvil9P8KTjPmxNHsgQbb0lJO2pSgRxctIDarhYM=";

    LocalDate VISA_EXPIRATION = LocalDate.of(2026, 12, 31);
    LocalDate MASTERCARD_EXPIRATION = LocalDate.of(2025, 10, 31);
    LocalDate AMEX_EXPIRATION = LocalDate.of(2024, 5, 31);
    LocalDate BANK_SPECIFIC_EXPIRATION = LocalDate.of(2023, 1, 31);

    BigDecimal VISA_BALANCE = new BigDecimal("100000.00");
    BigDecimal MASTERCARD_BALANCE = new BigDecimal("5000.00");
    BigDecimal AMEX_BALANCE = new BigDecimal("20000.00");
    BigDecimal BANK_SPECIFIC_BALANCE = BigDecimal.ZERO;

    // Переводы
    BigDecimal TRANSFER_1_AMOUNT = new BigDecimal("1000.00");
    BigDecimal TRANSFER_2_AMOUNT = new BigDecimal("500.00");

    LocalDateTime TRANSFER_1_CREATED = LocalDateTime.of(2023, 10, 1, 12, 0);
    LocalDateTime TRANSFER_1_CONFIRMED = LocalDateTime.of(2023, 10, 1, 12, 5);
    LocalDateTime TRANSFER_2_CREATED = LocalDateTime.of(2023, 10, 2, 10, 0);

    // Резервные счета
    BigDecimal BACKUP_ACCOUNT_1_AMOUNT = new BigDecimal("10000.00");
    BigDecimal BACKUP_ACCOUNT_2_AMOUNT = new BigDecimal("5000.00");

    String BACKUP_ORG_JSON = "{\"ORGANIZATION_INN\":\"1234567890\",\"FULL_NAME\":\"Company Ltd\","
            + "\"DOCUMENT_TYPE\":\"Certificate\",\"DOCUMENT_NUMBER\":\"123\","
            + "\"DOCUMENT_ISSUED_BY\":\"State\",\"ADDRESS\":\"Moscow\","
            + "\"PHONE\":\"1234567\",\"PAYMENT_PURPOSE\":\"Charity\"}";

    String BACKUP_INDIVIDUAL_JSON = "{\"FULL_NAME\":\"Ivan Ivanov\",\"DOCUMENT_TYPE\":\"Passport\","
            + "\"DOCUMENT_NUMBER\":\"123456\",\"DOCUMENT_ISSUED_BY\":\"Police\","
            + "\"PHONE\":\"7654321\",\"PAYMENT_PURPOSE\":\"Gift\"}";

    // Refresh токены
    String ADMIN_REFRESH_TOKEN = "admin_refresh_token";
    String USER_REFRESH_TOKEN = "user_refresh_token";

    // ID объектов
    Long ADMIN_ID = 1L;
    Long REGULAR_USER_ID = 2L;

    Long VISA_CARD_ID_OWNER_ADMIN = 1L;
    Long MASTERCARD_CARD_ID_OWNER_ADMIN = 2L;
    Long AMEX_CARD_ID_OWNER_REGULAR = 3L;
    Long BANK_SPECIFIC_CARD_ID_OWNER_REGULAR = 4L;

    Long TRANSFER_1_ID = 1L;
    Long TRANSFER_2_ID = 2L;

    Long BACKUP_ACCOUNT_1_ID = 1L;
    Long BACKUP_ACCOUNT_2_ID = 2L;

    // Роли
    Integer ROLE_USER_ID = 1;
    Integer ROLE_ADMIN_ID = 2;

    String ADMIN_CRED = "{\"username\": \"" + ADMIN_USERNAME + "\", \"password\": \"" + ADMIN_RAW_PASSWORD + "\"}";
    String RANDOM_CRED = "{\"username\": \"%s\", \"password\": \"%s\"}";


    // Остальные данные
    String SENDER_FULL_NAME = "John Doe";
    String SENDER_DOCUMENT_TYPE = "Passport";
    String SENDER_DOCUMENT_NUMBER = "123456789";
    String SENDER_DOCUMENT_ISSUED_BY = "Gov Authority";
    String SENDER_PHONE = "+1234567890";
    String SENDER_PAYMENT_PURPOSE = "Payment for services";

    String ORG_INN = "1234567890";
    String ORG_NAME = "Company Ltd";
    String ORG_DOCUMENT_TYPE = "Certificate";
    String ORG_DOCUMENT_NUMBER = "123";
    String ORG_DOCUMENT_ISSUED_BY = "State";
    String ORG_ADDRESS = "Moscow";
    String ORG_PHONE = "1234567";
    String ORG_PURPOSE = "Charity";

    String INDIVIDUAL_NAME = "Ivan Ivanov";
    String INDIVIDUAL_DOCUMENT_TYPE = "Passport";
    String INDIVIDUAL_DOCUMENT_NUMBER = "123456";
    String INDIVIDUAL_DOCUMENT_ISSUED_BY = "Police";
    String INDIVIDUAL_PHONE = "7654321";
    String INDIVIDUAL_PURPOSE = "Gift";
}