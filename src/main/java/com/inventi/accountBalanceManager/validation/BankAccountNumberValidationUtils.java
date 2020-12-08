package com.inventi.accountBalanceManager.validation;

import java.math.BigInteger;

import static com.inventi.accountBalanceManager.fixtures.Fixtures.DEFINITIONS;
import static java.util.Locale.ROOT;

public class BankAccountNumberValidationUtils {
    public static void validateAccNumber(String accNumber) throws IllegalAccessException {

        String accountNumber = accNumber.replaceAll("\\s", "").toUpperCase(ROOT);
        if (checkLenghtAndSymbols(accountNumber)) {
            throw new IllegalAccessException("Incorrect account number length or country code " + accNumber);
        }
        accountNumber = accountNumber.substring(4) + accountNumber.substring(0, 4);
        StringBuilder sb = convertToInteger(accountNumber);
        if (!checkRemainder(sb)) {
            throw new IllegalAccessException("Account number does not exist " + accNumber);
        }
    }

    private static boolean checkRemainder(StringBuilder sb) {
        BigInteger bigInt = new BigInteger(sb.toString());
        return bigInt.mod(BigInteger.valueOf(97)).intValue() == 1;
    }

    private static StringBuilder convertToInteger(String accountNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accountNumber.length(); i++)
            sb.append(Character.digit(accountNumber.charAt(i), 36));
        return sb;
    }

    private static boolean checkLenghtAndSymbols(String accountNumber) {
        return accountNumber.length() < 4 || !accountNumber.matches("[0-9A-Z]+") ||
                DEFINITIONS.getOrDefault(accountNumber.substring(0, 2), 0) != accountNumber.length();
    }
}
