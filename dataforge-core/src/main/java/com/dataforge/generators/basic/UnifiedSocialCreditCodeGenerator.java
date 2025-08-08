package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Unified Social Credit Code generator for generating Chinese enterprise identifiers.
 * 
 * Format:
 * - 1 digit for registration department code
 * - 1 digit for institution category code
 * - 6 digits for administrative division code
 * - 9 digits for entity identifier
 * - 1 digit for check code
 */
public class UnifiedSocialCreditCodeGenerator implements DataGenerator<String> {
    
    // Registration department codes
    private static final String[] REGISTRATION_DEPARTMENT_CODES = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "T", "U", "W", "X", "Y"
    };
    
    // Institution category codes
    private static final String[] INSTITUTION_CATEGORY_CODES = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "T", "U", "W", "X", "Y"
    };
    
    // Sample administrative division codes (in a real implementation, this would be comprehensive)
    private static final String[] ADMINISTRATIVE_DIVISION_CODES = {
        "110000", "120000", "310000", "500000",  // Municipalities
        "130000", "140000", "150000",            // Northern provinces
        "210000", "220000", "230000",
        "320000", "330000", "340000", "350000", "360000", "370000",
        "410000", "420000", "430000", "440000", "450000", "460000",
        "510000", "520000", "530000", "540000",
        "610000", "620000", "630000", "640000", "650000"
    };
    
    // Character set and weights for check code calculation
    private static final String CHARACTER_SET = "0123456789ABCDEFGHJKLMNPQRTUWXY";
    private static final int[] WEIGHTS = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
    
    private final boolean valid;
    
    public UnifiedSocialCreditCodeGenerator() {
        this(true);
    }
    
    public UnifiedSocialCreditCodeGenerator(boolean valid) {
        this.valid = valid;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // 1. Generate registration department code
        String registrationDepartmentCode = REGISTRATION_DEPARTMENT_CODES[
            random.nextInt(REGISTRATION_DEPARTMENT_CODES.length)];
        
        // 2. Generate institution category code
        String institutionCategoryCode = INSTITUTION_CATEGORY_CODES[
            random.nextInt(INSTITUTION_CATEGORY_CODES.length)];
        
        // 3. Generate administrative division code
        String administrativeDivisionCode = ADMINISTRATIVE_DIVISION_CODES[
            random.nextInt(ADMINISTRATIVE_DIVISION_CODES.length)];
        
        // 4. Generate entity identifier (9 digits)
        StringBuilder entityIdentifier = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            entityIdentifier.append(random.nextInt(10));
        }
        
        // 5. Generate check code
        String codeWithoutCheck = registrationDepartmentCode + institutionCategoryCode + 
                                 administrativeDivisionCode + entityIdentifier.toString();
        
        String checkCode;
        if (valid) {
            checkCode = calculateCheckCode(codeWithoutCheck);
        } else {
            // Generate an invalid check code
            do {
                checkCode = String.valueOf(CHARACTER_SET.charAt(random.nextInt(CHARACTER_SET.length())));
            } while (checkCode.equals(calculateCheckCode(codeWithoutCheck)));
        }
        
        return codeWithoutCheck + checkCode;
    }
    
    /**
     * Calculate the check code according to the GB32100-2015 standard.
     * 
     * @param codeWithoutCheck The 17-character code without the check digit
     * @return The check code character
     */
    private String calculateCheckCode(String codeWithoutCheck) {
        int sum = 0;
        
        // Calculate weighted sum
        for (int i = 0; i < 17; i++) {
            char c = codeWithoutCheck.charAt(i);
            int codeIndex = CHARACTER_SET.indexOf(c);
            if (codeIndex == -1) {
                // This shouldn't happen with valid inputs
                throw new IllegalArgumentException("Invalid character in code: " + c);
            }
            sum += codeIndex * WEIGHTS[i];
        }
        
        // Calculate check code
        int remainder = sum % 31;
        int checkCodeIndex = (31 - remainder) % 31;
        
        return String.valueOf(CHARACTER_SET.charAt(checkCodeIndex));
    }
}