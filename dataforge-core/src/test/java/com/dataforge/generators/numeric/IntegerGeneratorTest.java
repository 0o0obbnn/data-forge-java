package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.*;

public class IntegerGeneratorTest {
    
    @Test
    public void testDefaultIntegerGeneration() {
        IntegerGenerator generator = new IntegerGenerator();
        GenerationContext context = new GenerationContext(1);
        
        Integer value = generator.generate(context);
        assertNotNull(value);
    }
    
    @Test
    public void testCustomIntegerGeneration() {
        IntegerGenerator generator = new IntegerGenerator(10, 20);
        GenerationContext context = new GenerationContext(100); // Generate 100 values to test range
        
        for (int i = 0; i < 100; i++) {
            int value = generator.generate(context);
            assertTrue(value >= 10);
            assertTrue(value <= 20);
        }
    }
    
    @Test
    public void testInvalidRange() {
        org.testng.Assert.assertThrows(IllegalArgumentException.class, () -> {
            new IntegerGenerator(20, 10);
        });
    }
}