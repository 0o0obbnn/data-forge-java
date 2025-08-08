package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 性别生成器，用于生成性别值。
 */
public class GenderGenerator implements DataGenerator<String> {
    
    private static final List<String> GENDERS = Arrays.asList("Male", "Female", "Other");
    
    private final double maleRatio;
    private final double otherRatio;
    
    public GenderGenerator() {
        this(0.49, 0.01); // 默认比例：49% 男性，50% 女性，1% 其他
    }
    
    public GenderGenerator(double maleRatio, double otherRatio) {
        if (maleRatio < 0 || maleRatio > 1 || otherRatio < 0 || otherRatio > 1) {
            throw new IllegalArgumentException("Ratios must be between 0 and 1");
        }
        if (maleRatio + otherRatio > 1) {
            throw new IllegalArgumentException("Sum of male and other ratios cannot exceed 1");
        }
        this.maleRatio = maleRatio;
        this.otherRatio = otherRatio;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        double value = random.nextDouble();
        
        if (value < maleRatio) {
            return "Male";
        } else if (value < maleRatio + (1 - maleRatio - otherRatio)) {
            return "Female";
        } else {
            return "Other";
        }
    }
}