package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Boundary/Extreme value generator for generating boundary and extreme values for different data types.
 */
public class BoundaryExtremeValueGenerator implements DataGenerator<Object> {
    
    // Types of boundary/extreme values to generate
    public enum ValueType {
        INTEGER_MIN,      // Integer minimum value
        INTEGER_MAX,      // Integer maximum value
        INTEGER_MIN_PLUS, // Integer minimum value + 1
        INTEGER_MAX_MINUS,// Integer maximum value - 1
        ZERO,             // Zero (for numeric types)
        NEGATIVE_ZERO,    // Negative zero (for floating point types)
        STRING_EMPTY,     // Empty string
        STRING_SINGLE,    // Single character string
        STRING_MAX,       // Maximum length string (implementation defined)
        STRING_MAX_MINUS, // Maximum length string - 1
        STRING_MAX_PLUS,  // Maximum length string + 1
        DATE_PAST_EXTREME,// Extreme past date
        DATE_FUTURE_EXTREME,// Extreme future date
        DATE_LEAP_YEAR    // Leap year date
    }
    
    private final ValueType valueType;
    private final int maxLength; // For string types
    
    public BoundaryExtremeValueGenerator() {
        this(ValueType.ZERO, 100);
    }
    
    public BoundaryExtremeValueGenerator(ValueType valueType) {
        this(valueType, 100);
    }
    
    public BoundaryExtremeValueGenerator(ValueType valueType, int maxLength) {
        this.valueType = valueType;
        this.maxLength = maxLength;
    }
    
    @Override
    public Object generate(GenerationContext context) {
        Random random = context.getRandom();
        
        switch (valueType) {
            case INTEGER_MIN:
                return Integer.MIN_VALUE;
            case INTEGER_MAX:
                return Integer.MAX_VALUE;
            case INTEGER_MIN_PLUS:
                return Integer.MIN_VALUE + 1;
            case INTEGER_MAX_MINUS:
                return Integer.MAX_VALUE - 1;
            case ZERO:
                return 0;
            case NEGATIVE_ZERO:
                return -0.0;
            case STRING_EMPTY:
                return "";
            case STRING_SINGLE:
                return String.valueOf((char) (random.nextInt(26) + 'a'));
            case STRING_MAX:
                return generateStringOfLength(maxLength);
            case STRING_MAX_MINUS:
                return generateStringOfLength(maxLength - 1);
            case STRING_MAX_PLUS:
                return generateStringOfLength(maxLength + 1);
            case DATE_PAST_EXTREME:
                return "0001-01-01"; // Extreme past date
            case DATE_FUTURE_EXTREME:
                return "9999-12-31"; // Extreme future date
            case DATE_LEAP_YEAR:
                // Return a leap year date (e.g., Feb 29)
                return "2024-02-29";
            default:
                return 0;
        }
    }
    
    private String generateStringOfLength(int length) {
        if (length <= 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append('a');
        }
        return sb.toString();
    }
}