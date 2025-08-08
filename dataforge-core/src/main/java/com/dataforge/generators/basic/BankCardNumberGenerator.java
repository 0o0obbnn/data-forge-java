package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Bank Card Number generator for generating bank card numbers with Luhn algorithm validation.
 */
public class BankCardNumberGenerator implements DataGenerator<String> {
    
    // Sample BIN codes (Bank Identification Numbers) for different card types
    private static final String[][] BIN_CODES = {
        // Visa: starts with 4, length 16
        {"4", "16"},
        // MasterCard: starts with 5, length 16
        {"5", "16"},
        // UnionPay: starts with 62, length 16-19
        {"62", "16"},
        {"62", "17"},
        {"62", "18"},
        {"62", "19"},
        // American Express: starts with 34 or 37, length 15
        {"34", "15"},
        {"37", "15"}
    };
    
    public enum CardType {
        DEBIT, CREDIT, ANY
    }
    
    public enum Issuer {
        VISA, MASTERCARD, UNIONPAY, AMEX, ANY
    }
    
    private final CardType cardType;
    private final Issuer issuer;
    private final boolean valid;
    
    public BankCardNumberGenerator() {
        this(CardType.ANY, Issuer.ANY, true);
    }
    
    public BankCardNumberGenerator(CardType cardType, Issuer issuer, boolean valid) {
        this.cardType = cardType;
        this.issuer = issuer;
        this.valid = valid;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Select a random BIN code based on issuer
        String[] selectedBin = selectBin(random);
        String binPrefix = selectedBin[0];
        int totalLength = Integer.parseInt(selectedBin[1]);
        
        // Generate the main part of the card number (excluding the check digit)
        StringBuilder cardNumber = new StringBuilder(binPrefix);
        
        // Generate the remaining digits (excluding the check digit)
        int remainingLength = totalLength - binPrefix.length() - 1;
        for (int i = 0; i < remainingLength; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        // Calculate and append the check digit using the Luhn algorithm
        if (valid) {
            int checkDigit = calculateLuhnCheckDigit(cardNumber.toString());
            cardNumber.append(checkDigit);
        } else {
            // Generate an invalid check digit
            int checkDigit;
            do {
                checkDigit = random.nextInt(10);
            } while (checkDigit == calculateLuhnCheckDigit(cardNumber.toString()));
            cardNumber.append(checkDigit);
        }
        
        return cardNumber.toString();
    }
    
    private String[] selectBin(Random random) {
        if (issuer == Issuer.VISA) {
            return BIN_CODES[0]; // Visa
        } else if (issuer == Issuer.MASTERCARD) {
            return BIN_CODES[1]; // MasterCard
        } else if (issuer == Issuer.UNIONPAY) {
            // Return a random UnionPay BIN
            int index = 2 + random.nextInt(4);
            return BIN_CODES[index];
        } else if (issuer == Issuer.AMEX) {
            // Return a random American Express BIN
            int index = 6 + random.nextInt(2);
            return BIN_CODES[index];
        } else {
            // Return any BIN
            return BIN_CODES[random.nextInt(BIN_CODES.length)];
        }
    }
    
    /**
     * Calculate the check digit using the Luhn algorithm.
     * 
     * @param cardNumber The card number without the check digit
     * @return The check digit
     */
    private int calculateLuhnCheckDigit(String cardNumber) {
        int sum = 0;
        boolean alternate = true; // Start with the second-to-last digit
        
        // Process digits from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        // The check digit is the number needed to make the sum a multiple of 10
        return (sum * 9) % 10;
    }
}