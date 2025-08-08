package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import java.util.Random;

/**
 * Generates random blood types.
 */
public class BloodTypeGenerator implements DataGenerator<String> {

    public static final String[] BLOOD_TYPES = {
        "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };

    private final Random random = new Random();

    /**
     * Generates a random blood type from a predefined list.
     * @param context The generation context (not used by this generator).
     * @return A randomly selected blood type as a String.
     */
    @Override
    public String generate(GenerationContext context) {
        return BLOOD_TYPES[random.nextInt(BLOOD_TYPES.length)];
    }
}
