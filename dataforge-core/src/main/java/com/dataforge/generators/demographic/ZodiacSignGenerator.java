package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates a zodiac sign.
 */
public class ZodiacSignGenerator implements DataGenerator<String> {

    private static final List<String> ZODIAC_SIGNS = Arrays.asList(
            "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座",
            "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"
    );

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        return ZODIAC_SIGNS.get(random.nextInt(ZODIAC_SIGNS.size()));
    }
}
