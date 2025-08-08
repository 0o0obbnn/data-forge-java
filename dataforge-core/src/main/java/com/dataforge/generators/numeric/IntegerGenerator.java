package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Integer generator for generating integers within a specified range.
 */
public class IntegerGenerator implements DataGenerator<Integer> {
    
    private final int min;
    private final int max;
    
    public IntegerGenerator() {
        this(-1000, 1000); // Use a reasonable default range instead of full integer range
    }
    
    public IntegerGenerator(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than max");
        }
        this.min = min;
        this.max = max;
    }
    
    @Override
    public Integer generate(GenerationContext context) {
        Random random = context.getRandom();
        return min + random.nextInt(max - min + 1);
    }
}