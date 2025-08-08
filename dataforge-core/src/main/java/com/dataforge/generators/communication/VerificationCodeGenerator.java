package com.dataforge.generators.communication;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates verification codes for SMS or email.
 */
public class VerificationCodeGenerator implements DataGenerator<String> {

    private final Random random = new Random();

    @Override
    public String generate(GenerationContext context) {
        int length = (int) context.getParameter("length", 6);
        String chars = (String) context.getParameter("chars", "NUMERIC");

        StringBuilder code = new StringBuilder();
        String charSet = getCharSet(chars);

        for (int i = 0; i < length; i++) {
            code.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return code.toString();
    }

    private String getCharSet(String chars) {
        switch (chars.toUpperCase()) {
            case "ALPHANUMERIC":
                return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            case "ALPHA":
                return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            case "NUMERIC":
            default:
                return "0123456789";
        }
    }
}
