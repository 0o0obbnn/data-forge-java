package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates a religion from a predefined list.
 */
public class ReligionGenerator implements DataGenerator<String> {

    private static final List<String> RELIGIONS = Arrays.asList(
            "佛教", "基督教", "伊斯兰教", "道教", "印度教", "犹太教", "无"
    );
    
    private final double noneRatio;

    /**
     * Creates a generator with a default 70% probability of generating "无" (None).
     */
    public ReligionGenerator() {
        this(0.7);
    }

    /**
     * Creates a generator with a specific probability for generating "无" (None).
     *
     * @param noneRatio The probability (0.0 to 1.0) of generating "无".
     */
    public ReligionGenerator(double noneRatio) {
        if (noneRatio < 0.0 || noneRatio > 1.0) {
            throw new IllegalArgumentException("None ratio must be between 0.0 and 1.0.");
        }
        this.noneRatio = noneRatio;
    }

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        if (random.nextDouble() < noneRatio) {
            return "无";
        } else {
            // Exclude "None" from the random selection
            return RELIGIONS.get(random.nextInt(RELIGIONS.size() - 1));
        }
    }
}
