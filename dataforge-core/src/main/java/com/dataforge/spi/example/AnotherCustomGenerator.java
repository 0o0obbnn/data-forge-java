package com.dataforge.spi.example;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 另一个自定义生成器示例
 */
public class AnotherCustomGenerator implements DataGenerator<String> {

    private static final String[] CATEGORIES = {"Electronics", "Clothing", "Books", "Home", "Sports"};
    private final Random random = new Random();

    @Override
    public String generate(GenerationContext context) {
        String category = (String) context.getParameter("category", CATEGORIES[random.nextInt(CATEGORIES.length)]);
        int id = (Integer) context.getParameter("id", random.nextInt(10000));
        
        return String.format("%s-%05d", category.toUpperCase(), id);
    }

    @Override
    public String getName() {
        return "product_code";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("category", "id");
    }
}