package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import java.util.Random;

/**
 * Generates random religions.
 */
public class ReligionGenerator implements DataGenerator<String> {

    public static final String[] RELIGIONS = {
        "基督教", "伊斯兰教", "印度教", "佛教", "犹太教", "无宗教信仰"
    };

    private final Random random = new Random();

    /**
     * Generates a random religion from a predefined list.
     * @param context The generation context (not used by this generator).
     * @return A randomly selected religion as a String.
     */
    @Override
    public String generate(GenerationContext context) {
        return RELIGIONS[random.nextInt(RELIGIONS.length)];
    }
}
