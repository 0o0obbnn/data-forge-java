package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import java.util.Random;

/**
 * Generates random marital statuses.
 */
public class MaritalStatusGenerator implements DataGenerator<String> {

    public static final String[] MARITAL_STATUSES = {
        "未婚", "已婚", "离异", "丧偶"
    };

    private final Random random = new Random();

    /**
     * Generates a random marital status from a predefined list.
     * @param context The generation context (not used by this generator).
     * @return A randomly selected marital status as a String.
     */
    @Override
    public String generate(GenerationContext context) {
        return MARITAL_STATUSES[random.nextInt(MARITAL_STATUSES.length)];
    }
}
