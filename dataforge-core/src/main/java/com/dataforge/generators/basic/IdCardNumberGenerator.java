package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * ID Card Number generator for generating Chinese resident ID card numbers (18 digits).
 * 
 * Format: 
 * - 6 digits for region code
 * - 8 digits for birth date (YYYYMMDD)
 * - 3 digits for sequence code
 * - 1 digit for check code
 */
public class IdCardNumberGenerator implements DataGenerator<String> {
    
    // Sample region codes (in a real implementation, this would be a comprehensive list)
    private static final String[] REGION_CODES = {
        "110000", "120000", "310000", "500000",  // Municipalities
        "130100", "140100", "150100",            // Provinces
        "210100", "220100", "230100",
        "320100", "330100", "340100", "350100", "360100", "370100",
        "410100", "420100", "430100", "440100", "450100", "460100",
        "510100", "520100", "530100", "540100",
        "610100", "620100", "630100", "640100", "650100"
    };
    
    // Birth date range
    private final LocalDate minDate;
    private final LocalDate maxDate;
    
    // Gender restriction
    private final Gender gender;
    
    public enum Gender {
        MALE, FEMALE, ANY
    }
    
    public IdCardNumberGenerator() {
        this(LocalDate.now().minusYears(45), LocalDate.now().minusYears(18), Gender.ANY);
    }
    
    public IdCardNumberGenerator(LocalDate minDate, LocalDate maxDate, Gender gender) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.gender = gender;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // 1. Generate region code
        String regionCode = REGION_CODES[random.nextInt(REGION_CODES.length)];
        
        // 2. Generate birth date
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay = minDay + random.nextInt((int) (maxDay - minDay + 1));
        LocalDate birthDate = LocalDate.ofEpochDay(randomDay);
        String birthDateStr = birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 3. Generate sequence code (3 digits)
        // The second to last digit indicates gender (odd for male, even for female)
        int sequence;
        if (gender == Gender.MALE) {
            // Ensure odd number for male
            sequence = random.nextInt(500) * 2 + 1;
        } else if (gender == Gender.FEMALE) {
            // Ensure even number for female
            sequence = random.nextInt(500) * 2;
        } else {
            // Any gender
            sequence = random.nextInt(1000);
        }
        
        // Format sequence code as 3 digits
        String sequenceCode = String.format("%03d", sequence);
        
        // 4. Generate check code
        String idWithoutCheck = regionCode + birthDateStr + sequenceCode;
        char checkCode = calculateCheckCode(idWithoutCheck);
        
        return idWithoutCheck + checkCode;
    }
    
    /**
     * Calculate the check code according to the Chinese ID card standard.
     * 
     * @param idWithoutCheck The 17-digit ID without the check code
     * @return The check code character
     */
    private char calculateCheckCode(String idWithoutCheck) {
        // Weights for each of the first 17 digits
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        
        // Check code mapping
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        // Calculate the sum of products
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idWithoutCheck.charAt(i) - '0') * weights[i];
        }
        
        // Calculate check code index
        int remainder = sum % 11;
        return checkCodes[remainder];
    }
}