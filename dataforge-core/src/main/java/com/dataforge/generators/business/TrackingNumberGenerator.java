package com.dataforge.generators.business;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates a simulated logistics tracking number.
 */
public class TrackingNumberGenerator implements DataGenerator<String> {

    private final int length;
    private final String prefix;

    /**
     * Creates a generator for a 12-digit tracking number.
     */
    public TrackingNumberGenerator() {
        this(12, "");
    }

    /**
     * Creates a generator for a tracking number with a specified length and prefix.
     *
     * @param length The length of the random part of the number.
     * @param prefix A prefix to be added to the number.
     */
    public TrackingNumberGenerator(int length, String prefix) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }
        this.length = length;
        this.prefix = prefix == null ? "" : prefix;
    }

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return prefix + sb.toString();
    }
}
