package com.dataforge.generators.business;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates a 9-digit Chinese Organization Code (组织机构代码).
 * This is for legacy systems, as it has been largely replaced by the Unified Social Credit Code.
 * The code consists of 8 characters (body) and 1 check digit, conforming to the GB 11714-1997 standard.
 */
public class OrganizationCodeGenerator implements DataGenerator<String> {

    private static final String CHARACTER_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int[] WEIGHTS = {3, 7, 9, 10, 5, 8, 4, 2};
    private final boolean valid;

    /**
     * Creates a generator that produces valid organization codes by default.
     */
    public OrganizationCodeGenerator() {
        this(true);
    }

    /**
     * Creates a generator that can produce either valid or invalid organization codes.
     *
     * @param valid If true, generates codes that pass the checksum validation.
     *              If false, generates codes with an incorrect checksum.
     */
    public OrganizationCodeGenerator(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        StringBuilder body = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            body.append(CHARACTER_SET.charAt(random.nextInt(CHARACTER_SET.length())));
        }

        String bodyStr = body.toString();
        char checkDigit = calculateCheckDigit(bodyStr);

        if (valid) {
            return bodyStr + "-" + checkDigit;
        } else {
            char invalidCheckDigit;
            do {
                invalidCheckDigit = CHARACTER_SET.charAt(random.nextInt(11)); // 0-9 or X
            } while (invalidCheckDigit == checkDigit);
            return bodyStr + "-" + invalidCheckDigit;
        }
    }

    /**
     * Calculates the check digit for an 8-character organization code body.
     *
     * @param body The 8-character alphanumeric string.
     * @return The calculated check digit ('0'-'9' or 'X').
     */
    public static char calculateCheckDigit(String body) {
        if (body == null || body.length() != 8) {
            throw new IllegalArgumentException("Organization code body must be 8 characters long.");
        }

        int sum = 0;
        for (int i = 0; i < 8; i++) {
            char c = body.charAt(i);
            int value = CHARACTER_SET.indexOf(c);
            if (value == -1) {
                throw new IllegalArgumentException("Invalid character in organization code body: " + c);
            }
            sum += value * WEIGHTS[i];
        }

        int checkValue = 11 - (sum % 11);
        switch (checkValue) {
            case 10:
                return 'X';
            case 11:
                return '0';
            default:
                return (char) ('0' + checkValue);
        }
    }
}
