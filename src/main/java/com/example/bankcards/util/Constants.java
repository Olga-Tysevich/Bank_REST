package com.example.bankcards.util;

/**
 * The application constants interface.
 */
public interface Constants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_TYPE = "Bearer";
    String REFRESH_TOKEN_KEY = "refresh-token";
    String ID_CLAIM = "id";
    String ROLE_CLAIM = "role";

    String CARD_NUMBER_REGEX = "^(2200|2201|2202|4[0-9]{12,15}|5[1-5][0-9]{14}|3[47][0-9]{13})[0-9]{13,16}$";
    String CARD_NUMBER_MASK_REGEX = "^(\\d{4})(\\d{4})(\\d{4})(\\d{4})$";
    String ENROLMENT_TRANSFER_DETAILS = "from card id: %d, Reason: card is blocked for transfers.";

    String THE_ROLE_SET_CANNOT_BE_EMPTY = "The role set cannot be empty! You must select at least one user role!";
    String ID_CANNOT_BE_NULL = "ID cannot be null!";
    String SENDER_DATA_CANNOT_BE_NULL_OR_EMPTY = "ID cannot be null or empty!";
    String ROLE_CANNOT_BE_NULL = "Role cannot be null!";
    String EXPIRATION_DATE_CANNOT_BE_NULL = "End date cannot be null.";
    String USER_CANNOT_BE_NULL = "User must be specified";
    String CARD_NUMBER_CANNOT_BE_NULL = "Cart number cannot be null";
    String NAME_CANNOT_BE_EMPTY = "Name cannot be null or empty!";
    String NAME_CANNOT_BE_GZ_500 = "Name cannot exceed 500 characters";
    String DATE_OF_BIRTH_MUST_BE_IN_PAST = "Date of birth must be in the past";
    String INVALID_PASSWORD_LENGTH = "Password must be between 8 and 500 characters";
    String INVALID_CARD_NUMBER_MESSAGE = "The card number must be in the format for the Russian Federation or the Republic of Belarus (for example, 2200xxxxxxxxxxxx).";
    String USERNAME_CANNOT_BE_NULL_OR_EMPTY = "Email cannot be null or empty!";
    String REFRESH_TOKEN_CANNOT_BE_NULL_OR_EMPTY = "Refresh token cannot be null or empty!";
    String CARD_FROM_CANNOT_BE_NULL = "Card from cannot be null!";
    String CARD_TO_CANNOT_BE_NULL = "Card to cannot be null!";
    String PASSWORD_CANNOT_BE_NULL_OR_EMPTY = "Password cannot be null or empty!";
    String ACCESS_DENIED = "Access Denied";
    String ACCESS_DENIED_MESSAGE = "User do not have permission to access this resource.";
    String NOT_AUTHORIZED = "Access Denied";
    String UNKNOWN_USER = "Unknown index";
    String TOKEN_CANNOT_BE_NULL_OR_EMPTY = "Token cannot be null or empty!";
    String INVALID_REFRESH_TOKEN = "Invalid refresh token!";
    String BALANCE_MUST_BE_POSITIVE = "Balance must be positive or zero!";
    String HOLD_MUST_BE_POSITIVE = "Hold balance must be positive!";
    String HOLD_CANNOT_BE_NULL = "Hold balance cannot be null";
    String TRANSFER_DATE_CANNOT_BE_NUL = "Transfer date cannot be null";
    String TRANSFER_DATE_MUST_BE_IN_PAST = "Transfer date must be in the past";
    String TRANSFER_CONFIRM_DATE_MUST_BE_IN_PAST = "Transfer confirm date must be in the past";
    String TRANSFER_STATUS_CANNOT_BE_NUL = "Transfer status cannot be null";
    String TRANSFER_AMOUNT_CANNOT_BE_NUL = "Transfer amount cannot be null";
    String TRANSFER_AMOUNT_BE_POSITIVE = "Hold balance must be positive!";
    String VERSION_CANNOT_BE_NULL = "Version must be specified";

    String USER_NOT_FOUND = "User not found!";

}