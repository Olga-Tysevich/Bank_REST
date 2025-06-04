package com.example.bankcards.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    public static String calculateLuhnDigit(String numberWithoutCheckDigit) {
        int sum = 0;
        boolean alternate = true;

        for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(numberWithoutCheckDigit.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return String.valueOf(checkDigit);
    }
}
