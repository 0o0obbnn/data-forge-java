package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates valid Legal Entity Identifier (LEI) codes based on ISO 17442.
 */
public class LeiCodeGenerator implements DataGenerator<String> {

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
        StringBuilder lei = new StringBuilder();
        // 4-digit LOU code
        for (int i = 0; i < 4; i++) {
            lei.append(random.nextInt(10));
        }
        // 2 reserved digits (00)
        lei.append("00");
        // 12-character entity-specific part
        for (int i = 0; i < 12; i++) {
            lei.append(randomChar());
        }
        String partialLei = lei.toString();
        String checkDigits = calculateCheckDigits(partialLei);
        return partialLei + checkDigits;
    }

    private String generateInvalid() {
        String lei = generateValid();
        // Invalidate the check digits
        char lastChar = lei.charAt(lei.length() - 1);
        char invalidLastChar = (lastChar == '9') ? '0' : (char) (lastChar + 1);
        return lei.substring(0, lei.length() - 1) + invalidLastChar;
    }

    private char randomChar() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return chars.charAt(random.nextInt(chars.length()));
    }

    private String calculateCheckDigits(String partialLei) {
        String numericLei = toNumericString(partialLei + "00");
        java.math.BigInteger num = new java.math.BigInteger(numericLei);
        int remainder = num.mod(java.math.BigInteger.valueOf(97)).intValue();
        int check = 98 - remainder;
        return String.format("%02d", check);
    }

    private String toNumericString(String str) {
        StringBuilder numeric = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                numeric.append(c);
            } else {
                numeric.append(Character.getNumericValue(c));
            }
        }
        return numeric.toString();
    }
}
