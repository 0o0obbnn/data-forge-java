package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import java.util.Random;

/**
 * Generates random zodiac signs.
 */
public class ZodiacSignGenerator implements DataGenerator<String> {

    public static final String[] ZODIAC_SIGNS = {
        "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", 
        "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"
    };

    private final Random random = new Random();

    /**
     * Generates a random zodiac sign from a predefined list.
     * @param context The generation context (not used by this generator).
     * @return A randomly selected zodiac sign as a String.
     */
    @Override
    public String generate(GenerationContext context) {
        return ZODIAC_SIGNS[random.nextInt(ZODIAC_SIGNS.length)];
    }
}
