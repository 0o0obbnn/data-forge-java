package com.dataforge.generators.business;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates a promotional coupon code.
 */
public class CouponCodeGenerator implements DataGenerator<String> {

    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int length;
    private final String prefix;

    /**
     * Creates a generator for an 8-character coupon code.
     */
    public CouponCodeGenerator() {
        this(8, "");
    }

    /**
     * Creates a generator for a coupon code with a specified length and prefix.
     *
     * @param length The length of the random part of the code.
     * @param prefix A prefix to be added to the code.
     */
    public CouponCodeGenerator(int length, String prefix) {
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
            sb.append(ALPHANUMERIC_CHARS.charAt(random.nextInt(ALPHANUMERIC_CHARS.length())));
        }
        return prefix + sb.toString();
    }
}
