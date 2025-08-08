package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Percentage/Rate generator for generating percentage values, interest rates, or exchange rates.
 */
public class PercentageRateGenerator implements DataGenerator<String> {
    
    private final double min;
    private final double max;
    private final int precision;
    private final boolean includePercentSign;
    
    /**
     * Creates a percentage rate generator with default settings (0-100% with 2 decimal places).
     */
    public PercentageRateGenerator() {
        this(0.0, 100.0, 2, true);
    }
    
    /**
     * Creates a percentage rate generator.
     * 
     * @param min minimum value
     * @param max maximum value (must be greater than min)
     * @param precision number of decimal places
     * @param includePercentSign whether to include the % sign in the output
     */
    public PercentageRateGenerator(double min, double max, int precision, boolean includePercentSign) {
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        if (precision < 0) {
            throw new IllegalArgumentException("Precision must be non-negative");
        }
        this.min = min;
        this.max = max;
        this.precision = precision;
        this.includePercentSign = includePercentSign;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a random value between min and max
        double range = max - min;
        double value = min + (random.nextDouble() * range);
        
        // Format the value with specified precision
        String format = "%." + precision + "f";
        String formattedValue = String.format(format, value);
        
        // Add percent sign if requested
        if (includePercentSign) {
            return formattedValue + "%";
        }
        
        return formattedValue;
    }
    
    @Override
    public String getName() {
        return "percentage_rate";
    }
}