package com.dataforge.spi.example;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 自定义数据生成器示例
 */
public class CustomDataGenerator implements DataGenerator<String> {

    private static final String[] CUSTOM_VALUES = {
        "CUSTOM_VALUE_1", "CUSTOM_VALUE_2", "CUSTOM_VALUE_3", "CUSTOM_VALUE_4"
    };
    private final Random random = new Random();

    @Override
    public String generate(GenerationContext context) {
        String prefix = (String) context.getParameter("prefix", "CUSTOM");
        int length = (Integer) context.getParameter("length", 8);
        
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + random.nextInt(26)));
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return "custom_value";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("prefix", "length");
    }
}