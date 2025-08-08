package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;

import java.util.Random;

/**
 * Invalid/exceptional data generator for generating data that violates format, range, or type constraints.
 * Useful for testing error handling and validation in applications.
 */
public class InvalidExceptionDataGenerator implements DataGenerator<String> {
    
    private final Random random;
    
    /**
     * Creates an invalid/exceptional data generator with default settings.
     */
    public InvalidExceptionDataGenerator() {
        this.random = new Random();
    }
    
    @Override
    public String generate(GenerationContext context) {
        // Get parameters
        Object dataTypeObj = context.getParameter("data_type", "string");
        String dataType = dataTypeObj.toString().toLowerCase();
        
        Object violationTypeObj = context.getParameter("violation_type", "format");
        String violationType = violationTypeObj.toString().toLowerCase();
        
        switch (dataType) {
            case "phone":
                return generateInvalidPhone(violationType);
            case "email":
                return generateInvalidEmail(violationType);
            case "idcard":
                return generateInvalidIdCard(violationType);
            case "bankcard":
                return generateInvalidBankCard(violationType);
            case "integer":
                return generateInvalidInteger(violationType);
            case "decimal":
                return generateInvalidDecimal(violationType);
            case "date":
                return generateInvalidDate(violationType);
            case "string":
            default:
                return generateInvalidString(violationType);
        }
    }
    
    private String generateInvalidPhone(String violationType) {
        switch (violationType) {
            case "length":
                // Generate phone number with wrong length
                int wrongLength = random.nextBoolean() ? random.nextInt(10) : 12 + random.nextInt(5);
                StringBuilder phone = new StringBuilder();
                for (int i = 0; i < wrongLength; i++) {
                    phone.append(random.nextInt(10));
                }
                return phone.toString();
            case "format":
            default:
                // Generate phone number with invalid characters
                return "123-" + random.nextInt(1000) + "-abcd";
        }
    }
    
    private String generateInvalidEmail(String violationType) {
        switch (violationType) {
            case "format":
            default:
                String[] invalidEmails = {
                    "user@",
                    "@domain.com",
                    "user@domain",
                    "user.domain.com",
                    "user@domain..com",
                    "user@@domain.com",
                    "user@domain.c",
                    "user name@domain.com"
                };
                return invalidEmails[random.nextInt(invalidEmails.length)];
            case "length":
                // Generate very long email
                StringBuilder email = new StringBuilder();
                int nameLength = 100 + random.nextInt(100);
                for (int i = 0; i < nameLength; i++) {
                    email.append((char) ('a' + random.nextInt(26)));
                }
                email.append("@");
                int domainLength = 50 + random.nextInt(50);
                for (int i = 0; i < domainLength; i++) {
                    email.append((char) ('a' + random.nextInt(26)));
                }
                email.append(".com");
                return email.toString();
        }
    }
    
    private String generateInvalidIdCard(String violationType) {
        switch (violationType) {
            case "length":
                // Generate ID card with wrong length
                int wrongLength = 15 + random.nextInt(5); // 15, 16, 17, or 18 (18 is valid)
                if (wrongLength == 18) wrongLength = 19; // Make sure it's invalid
                StringBuilder idCard = new StringBuilder();
                for (int i = 0; i < wrongLength; i++) {
                    idCard.append(random.nextInt(10));
                }
                return idCard.toString();
            case "format":
            default:
                // Generate ID card with invalid characters
                return "123456abcd" + random.nextInt(1000);
        }
    }
    
    private String generateInvalidBankCard(String violationType) {
        switch (violationType) {
            case "length":
                // Generate bank card with wrong length
                int wrongLength = 10 + random.nextInt(10); // 10-19 (16-19 are typical)
                StringBuilder cardNumber = new StringBuilder();
                for (int i = 0; i < wrongLength; i++) {
                    cardNumber.append(random.nextInt(10));
                }
                return cardNumber.toString();
            case "format":
            default:
                // Generate bank card that fails Luhn algorithm
                StringBuilder cardNumber2 = new StringBuilder();
                for (int i = 0; i < 15; i++) {
                    cardNumber2.append(random.nextInt(10));
                }
                // Add a check digit that we know is wrong
                cardNumber2.append((cardNumber2.charAt(14) - '0' + 1) % 10);
                return cardNumber2.toString();
        }
    }
    
    private String generateInvalidInteger(String violationType) {
        switch (violationType) {
            case "format":
            default:
                // Generate non-numeric string
                String[] nonNumeric = {"abc", "12.34", "12a", " ", "-", "+", "1.0e5"};
                return nonNumeric[random.nextInt(nonNumeric.length)];
            case "range":
                // Generate integer outside typical ranges
                return String.valueOf(1000000000L + random.nextInt(1000000000));
        }
    }
    
    private String generateInvalidDecimal(String violationType) {
        switch (violationType) {
            case "format":
            default:
                // Generate invalid decimal format
                String[] invalidDecimals = {"abc", "12,34", "12.34.56", ".", " ", "12.", ".34"};
                return invalidDecimals[random.nextInt(invalidDecimals.length)];
            case "range":
                // Generate decimal with too many digits
                StringBuilder decimal = new StringBuilder("123456789012345678901234567890.");
                for (int i = 0; i < 50; i++) {
                    decimal.append(random.nextInt(10));
                }
                return decimal.toString();
        }
    }
    
    private String generateInvalidDate(String violationType) {
        switch (violationType) {
            case "format":
            default:
                // Generate invalid date formats
                String[] invalidDates = {
                    "2023/12/31",
                    "31-12-2023",
                    "2023.12.31",
                    "12/31/2023",
                    "20231231",
                    "abcd",
                    " ",
                    "2023-13-01",
                    "2023-12-32",
                    "2023-02-30"
                };
                return invalidDates[random.nextInt(invalidDates.length)];
            case "range":
                // Generate date far in the future or past
                return random.nextBoolean() ? "0001-01-01" : "9999-12-31";
        }
    }
    
    private String generateInvalidString(String violationType) {
        switch (violationType) {
            case "length":
                // Generate very long string
                StringBuilder str = new StringBuilder();
                int length = 10000 + random.nextInt(10000);
                for (int i = 0; i < length; i++) {
                    str.append((char) ('a' + random.nextInt(26)));
                }
                return str.toString();
            case "format":
            default:
                // Generate string with special characters
                return "Hello\0World\n\t\r"; // String with control characters
        }
    }
    
    @Override
    public String getName() {
        return "invalid_exception_data";
    }
    
    @Override
    public java.util.List<String> getSupportedParameters() {
        return java.util.Arrays.asList("data_type", "violation_type");
    }
}