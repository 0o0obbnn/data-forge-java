package com.dataforge.spi.example;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.spi.DataForgeExtension;

import java.util.Arrays;
import java.util.List;

/**
 * 示例扩展实现
 * 展示如何通过SPI机制扩展DataForge功能
 */
public class ExampleExtension implements DataForgeExtension {

    @Override
    public String getName() {
        return "example-extension";
    }

    @Override
    public String getDescription() {
        return "Example extension demonstrating SPI integration";
    }

    @Override
    public List<Class<? extends DataGenerator>> getGeneratorClasses() {
        return Arrays.asList(
            CustomDataGenerator.class,
            AnotherCustomGenerator.class
        );
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public int getPriority() {
        return 100;
    }
}