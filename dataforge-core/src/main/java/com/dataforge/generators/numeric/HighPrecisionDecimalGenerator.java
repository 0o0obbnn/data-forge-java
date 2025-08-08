package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * High precision decimal generator for generating BigDecimal values with configurable precision.
 */
public class HighPrecisionDecimalGenerator implements DataGenerator<BigDecimal> {
    
    private final BigDecimal min;
    private final BigDecimal max;
    private final int scale;
    
    /**
     * Creates a high precision decimal generator with default settings.
     */
    public HighPrecisionDecimalGenerator() {
        this(new BigDecimal("-1000"), new BigDecimal("1000"), 10);
    }
    
    /**
     * Creates a high precision decimal generator.
     * 
     * @param min minimum value
     * @param max maximum value (must be greater than min)
     * @param scale number of decimal places
     */
    public HighPrecisionDecimalGenerator(BigDecimal min, BigDecimal max, int scale) {
        if (min.compareTo(max) >= 0) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        if (scale < 0) {
            throw new IllegalArgumentException("Scale must be non-negative");
        }
        this.min = min;
        this.max = max;
        this.scale = scale;
    }
    
    @Override
    public BigDecimal generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a random value between 0 and 1
        BigDecimal randomValue = new BigDecimal(random.nextDouble());
        
        // Scale to the range between min and max
        BigDecimal range = max.subtract(min);
        BigDecimal scaledValue = range.multiply(randomValue);
        BigDecimal result = min.add(scaledValue);
        
        // Set the scale (number of decimal places)
        return result.setScale(scale, RoundingMode.HALF_UP);
    }
    
    @Override
    public String getName() {
        return "high_precision_decimal";
    }
}