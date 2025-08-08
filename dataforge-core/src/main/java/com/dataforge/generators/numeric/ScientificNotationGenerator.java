package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Scientific notation generator for generating numbers in scientific notation format (e.g., 1.23E+10).
 */
public class ScientificNotationGenerator implements DataGenerator<String> {
    
    private final double minMantissa;
    private final double maxMantissa;
    private final int minExponent;
    private final int maxExponent;
    private final int precision;
    
    /**
     * Creates a scientific notation generator with default settings.
     */
    public ScientificNotationGenerator() {
        this(1.0, 9.99, -10, 10, 2);
    }
    
    /**
     * Creates a scientific notation generator.
     * 
     * @param minMantissa minimum mantissa value (must be positive)
     * @param maxMantissa maximum mantissa value (must be positive and greater than minMantissa)
     * @param minExponent minimum exponent value
     * @param maxExponent maximum exponent value (must be greater than minExponent)
     * @param precision number of decimal places for mantissa
     */
    public ScientificNotationGenerator(double minMantissa, double maxMantissa, 
                                     int minExponent, int maxExponent, int precision) {
        if (minMantissa <= 0 || maxMantissa <= 0) {
            throw new IllegalArgumentException("Both minMantissa and maxMantissa must be positive");
        }
        if (minMantissa >= maxMantissa) {
            throw new IllegalArgumentException("minMantissa must be less than maxMantissa");
        }
        if (minExponent >= maxExponent) {
            throw new IllegalArgumentException("minExponent must be less than maxExponent");
        }
        if (precision < 0) {
            throw new IllegalArgumentException("Precision must be non-negative");
        }
        this.minMantissa = minMantissa;
        this.maxMantissa = maxMantissa;
        this.minExponent = minExponent;
        this.maxExponent = maxExponent;
        this.precision = precision;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate mantissa
        double mantissaRange = maxMantissa - minMantissa;
        double mantissa = minMantissa + (random.nextDouble() * mantissaRange);
        
        // Generate exponent
        int exponent = minExponent + random.nextInt(maxExponent - minExponent + 1);
        
        // Format the result
        String format = "%." + precision + "fE%+d";
        return String.format(format, mantissa, exponent);
    }
    
    @Override
    public String getName() {
        return "scientific_notation";
    }
}