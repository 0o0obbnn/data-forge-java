package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates a numeric verification code, typically used for SMS or email verification.
 */
public class SmsVerificationCodeGenerator implements DataGenerator<String> {

    private final int length;

    /**
     * Creates a generator for a 6-digit verification code.
     */
    public SmsVerificationCodeGenerator() {
        this(6);
    }

    /**
     * Creates a generator for a verification code of a specified length.
     *
     * @param length The desired length of the verification code.
     */
    public SmsVerificationCodeGenerator(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }
        this.length = length;
    }

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public int getLength() {
        return length;
    }
}
