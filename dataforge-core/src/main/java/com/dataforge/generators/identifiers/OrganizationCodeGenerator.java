package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates valid Chinese Organization Codes based on GB 11714-1997.
 */
public class OrganizationCodeGenerator implements DataGenerator<String> {

    private static final int[] WEIGHTS = {3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] BASE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Random random = new Random();

    @Override
    public String generate(GenerationContext context) {
        boolean valid = (boolean) context.getParameter("valid", true);
        if (valid) {
            return generateValid();
        } else {
            return generateInvalid();
        }
    }

    private String generateValid() {
        char[] code = new char[8];
        for (int i = 0; i < 8; i++) {
            code[i] = BASE_CHARS[random.nextInt(BASE_CHARS.length)];
        }
        String codeStr = new String(code);
        char checkDigit = calculateCheckDigit(codeStr);
        return codeStr + "-" + checkDigit;
    }

    private String generateInvalid() {
        char[] code = new char[8];
        for (int i = 0; i < 8; i++) {
            code[i] = BASE_CHARS[random.nextInt(BASE_CHARS.length)];
        }
        String codeStr = new String(code);
        char checkDigit = calculateCheckDigit(codeStr);
        // Invalidate the check digit
        char invalidCheckDigit = (checkDigit == 'X') ? '0' : (char) (checkDigit + 1);
        if (invalidCheckDigit > '9' && invalidCheckDigit < 'A') {
            invalidCheckDigit = 'A';
        }
        return codeStr + "-" + invalidCheckDigit;
    }

    private char calculateCheckDigit(String code) {
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            char c = code.charAt(i);
            int value = Character.getNumericValue(c);
            sum += value * WEIGHTS[i];
        }

        int remainder = sum % 11;
        int checkValue = 11 - remainder;

        if (checkValue == 10) {
            return 'X';
        } else if (checkValue == 11) {
            return '0';
        } else {
            return (char) ('0' + checkValue);
        }
    }
}
