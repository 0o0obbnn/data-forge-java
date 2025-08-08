package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 年龄生成器，用于在指定范围内生成年龄。
 */
public class AgeGenerator implements DataGenerator<Integer> {
    
    private static final int DEFAULT_MIN_AGE = 18;
    private static final int DEFAULT_MAX_AGE = 65;
    
    private final int minAge;
    private final int maxAge;
    
    public AgeGenerator() {
        this(DEFAULT_MIN_AGE, DEFAULT_MAX_AGE);
    }
    
    public AgeGenerator(int minAge, int maxAge) {
        if (minAge < 0 || maxAge < 0) {
            throw new IllegalArgumentException("Ages must be non-negative");
        }
        if (minAge > maxAge) {
            throw new IllegalArgumentException("Min age cannot be greater than max age");
        }
        this.minAge = minAge;
        this.maxAge = maxAge;
    }
    
    @Override
    public Integer generate(GenerationContext context) {
        Random random = context.getRandom();
        return minAge + random.nextInt(maxAge - minAge + 1);
    }
}