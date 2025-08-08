package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Decimal generator for generating decimal numbers within a specified range and precision.
 */
public class DecimalGenerator implements DataGenerator<BigDecimal> {
    
    private final BigDecimal min;
    private final BigDecimal max;
    private final int scale;
    
    public DecimalGenerator() {
        this(BigDecimal.valueOf(-1000), BigDecimal.valueOf(1000), 2);
    }
    
    public DecimalGenerator(BigDecimal min, BigDecimal max, int scale) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min cannot be greater than max");
        }
        this.min = min;
        this.max = max;
        this.scale = scale;
    }
    
    @Override
    public BigDecimal generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a random decimal between min and max
        BigDecimal range = max.subtract(min);
        BigDecimal randomValue = range.multiply(BigDecimal.valueOf(random.nextDouble()));
        BigDecimal result = min.add(randomValue);
        
        return result.setScale(scale, RoundingMode.HALF_UP);
    }
}