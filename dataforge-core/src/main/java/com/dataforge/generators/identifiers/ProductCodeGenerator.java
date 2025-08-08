package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates various types of product codes like SKU, GTIN, ISBN, etc.
 */
public class ProductCodeGenerator implements DataGenerator<String> {

    private final Random random = new Random();

    @Override
    public String generate(GenerationContext context) {
        String type = (String) context.getParameter("type", "SKU");
        switch (type.toUpperCase()) {
            case "GTIN":
                return generateGtin();
            case "ISBN":
                return generateIsbn();
            case "ISSN":
                return generateIssn();
            case "SKU":
            default:
                return generateSku(context);
        }
    }

    private String generateSku(GenerationContext context) {
        int length = (int) context.getParameter("length", 8);
        StringBuilder sku = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sku.append(randomChar());
        }
        return sku.toString();
    }

    private String generateGtin() {
        // EAN-13
        char[] digits = new char[12];
        for (int i = 0; i < 12; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        String gtin = new String(digits);
        return gtin + calculateEan13CheckDigit(gtin);
    }

    private String generateIsbn() {
        // ISBN-13
        char[] digits = new char[12];
        digits[0] = '9';
        digits[1] = '7';
        digits[2] = '8'; // or 979
        for (int i = 3; i < 12; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        String isbn = new String(digits);
        return isbn + calculateEan13CheckDigit(isbn);
    }

    private String generateIssn() {
        char[] digits = new char[7];
        for (int i = 0; i < 7; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        String issn = new String(digits);
        return issn + calculateIssnCheckDigit(issn);
    }

    private char randomChar() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return chars.charAt(random.nextInt(chars.length()));
    }

    private char calculateEan13CheckDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            // Weights are 1, 3, 1, 3, ... from the left
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return (char) (checkDigit + '0');
    }

    private char calculateIssnCheckDigit(String issn) {
        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(issn.charAt(i)) * (8 - i);
        }
        int remainder = sum % 11;
        return (remainder == 0) ? '0' : ((11 - remainder) == 10) ? 'X' : (char) ((11 - remainder) + '0');
    }
}