package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Negative number generator for generating negative integers or decimals within a specified range.
 */
public class NegativeNumberGenerator implements DataGenerator<Number> {
    
    private final double min;
    private final double max;
    private final boolean integerOnly;
    
    /**
     * Creates a negative number generator with default settings (-1 to -1000).
     */
    public NegativeNumberGenerator() {
        this(-1000, -1, false);
    }
    
    /**
     * Creates a negative number generator.
     * 
     * @param min minimum value (must be negative)
     * @param max maximum value (must be negative and greater than min)
     * @param integerOnly whether to generate only integers
     */
    public NegativeNumberGenerator(double min, double max, boolean integerOnly) {
        if (min >= 0 || max >= 0) {
            throw new IllegalArgumentException("Both min and max must be negative");
        }
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than max");
        }
        this.min = min;
        this.max = max;
        this.integerOnly = integerOnly;
    }
    
    @Override
    public Number generate(GenerationContext context) {
        Random random = context.getRandom();
        double range = max - min;
        double value = min + (random.nextDouble() * range);
        
        if (integerOnly) {
            return (int) Math.floor(value);
        } else {
            return value;
        }
    }
    
    @Override
    public String getName() {
        return "negative_number";
    }
}