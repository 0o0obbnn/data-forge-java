package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import java.util.Random;

/**
 * Generates random education levels.
 */
public class EducationLevelGenerator implements DataGenerator<String> {

    public static final String[] EDUCATION_LEVELS = {
        "小学", "初中", "高中", "大专", "本科", "硕士", "博士"
    };

    private final Random random = new Random();

    /**
     * Generates a random education level from a predefined list.
     * @param context The generation context (not used by this generator).
     * @return A randomly selected education level as a String.
     */
    @Override
    public String generate(GenerationContext context) {
        return EDUCATION_LEVELS[random.nextInt(EDUCATION_LEVELS.length)];
    }
}
