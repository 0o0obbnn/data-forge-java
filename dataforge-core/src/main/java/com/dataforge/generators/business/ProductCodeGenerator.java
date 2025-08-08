package com.dataforge.generators.business;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates various types of product codes like EAN, UPC, and ISBN.
 */
public class ProductCodeGenerator implements DataGenerator<String> {

    public enum ProductCodeType {
        EAN13,
        UPCA,
        ISBN13
    }

    private final ProductCodeType type;
    private final boolean valid;

    public ProductCodeGenerator() {
        this(ProductCodeType.EAN13, true);
    }

    public ProductCodeGenerator(ProductCodeType type) {
        this(type, true);
    }

    public ProductCodeGenerator(ProductCodeType type, boolean valid) {
        this.type = type;
        this.valid = valid;
    }

    @Override
    public String generate(GenerationContext context) {
        switch (type) {
            case EAN13:
                return generateEan13(context.getRandom());
            case UPCA:
                return generateUpca(context.getRandom());
            case ISBN13:
                return generateIsbn13(context.getRandom());
            default:
                throw new UnsupportedOperationException("Product code type not supported: " + type);
        }
    }

    private String generateEan13(Random random) {
        StringBuilder body = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            body.append(random.nextInt(10));
        }
        String bodyStr = body.toString();
        char checkDigit = calculateEan13CheckDigit(bodyStr);

        if (valid) {
            return bodyStr + checkDigit;
        } else {
            char invalidCheckDigit;
            do {
                invalidCheckDigit = (char) ('0' + random.nextInt(10));
            } while (invalidCheckDigit == checkDigit);
            return bodyStr + invalidCheckDigit;
        }
    }

    private String generateUpca(Random random) {
        StringBuilder body = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            body.append(random.nextInt(10));
        }
        String bodyStr = body.toString();
        char checkDigit = calculateUpcaCheckDigit(bodyStr);

        if (valid) {
            return bodyStr + checkDigit;
        } else {
            char invalidCheckDigit;
            do {
                invalidCheckDigit = (char) ('0' + random.nextInt(10));
            } while (invalidCheckDigit == checkDigit);
            return bodyStr + invalidCheckDigit;
        }
    }

    private String generateIsbn13(Random random) {
        // ISBN-13 is essentially an EAN-13 code that starts with 978 or 979
        String prefix = random.nextBoolean() ? "978" : "979";
        StringBuilder body = new StringBuilder(9);
        for (int i = 0; i < 9; i++) {
            body.append(random.nextInt(10));
        }
        String fullBody = prefix + body.toString();
        char checkDigit = calculateEan13CheckDigit(fullBody);

        if (valid) {
            return fullBody + checkDigit;
        } else {
            char invalidCheckDigit;
            do {
                invalidCheckDigit = (char) ('0' + random.nextInt(10));
            } while (invalidCheckDigit == checkDigit);
            return fullBody + invalidCheckDigit;
        }
    }

    /**
     * Calculates the check digit for a 12-digit EAN-13 body.
     *
     * @param body The 12-digit string.
     * @return The calculated check digit.
     */
    public static char calculateEan13CheckDigit(String body) {
        if (body == null || body.length() != 12) {
            throw new IllegalArgumentException("EAN-13/ISBN-13 body must be 12 digits long.");
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(body.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int remainder = sum % 10;
        int checkValue = (remainder == 0) ? 0 : 10 - remainder;

        return (char) ('0' + checkValue);
    }

    /**
     * Calculates the check digit for an 11-digit UPC-A body.
     *
     * @param body The 11-digit string.
     * @return The calculated check digit.
     */
    public static char calculateUpcaCheckDigit(String body) {
        if (body == null || body.length() != 11) {
            throw new IllegalArgumentException("UPC-A body must be 11 digits long.");
        }

        int sum = 0;
        for (int i = 0; i < 11; i++) {
            int digit = Character.getNumericValue(body.charAt(i));
            sum += (i % 2 == 0) ? digit * 3 : digit;
        }

        int remainder = sum % 10;
        int checkValue = (remainder == 0) ? 0 : 10 - remainder;

        return (char) ('0' + checkValue);
    }
}
