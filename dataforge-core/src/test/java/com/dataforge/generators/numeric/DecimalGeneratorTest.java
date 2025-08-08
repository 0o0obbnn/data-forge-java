package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.*;

public class DecimalGeneratorTest {
    
    @Test
    public void testDefaultDecimalGeneration() {
        DecimalGenerator generator = new DecimalGenerator();
        GenerationContext context = new GenerationContext(1);
        
        BigDecimal value = generator.generate(context);
        assertNotNull(value);
        assertEquals(2, value.scale()); // Default scale is 2
    }
    
    @Test
    public void testCustomDecimalGeneration() {
        BigDecimal min = new BigDecimal("10.5");
        BigDecimal max = new BigDecimal("20.75");
        DecimalGenerator generator = new DecimalGenerator(min, max, 3);
        GenerationContext context = new GenerationContext(100); // Generate 100 values to test range
        
        for (int i = 0; i < 100; i++) {
            BigDecimal value = generator.generate(context);
            assertTrue(value.compareTo(min) >= 0);
            assertTrue(value.compareTo(max) <= 0);
            assertEquals(3, value.scale()); // Scale should be 3
        }
    }
    
    @Test
    public void testInvalidRange() {
        BigDecimal min = new BigDecimal("20.75");
        BigDecimal max = new BigDecimal("10.5");
        
        org.testng.Assert.assertThrows(IllegalArgumentException.class, () -> {
            new DecimalGenerator(min, max, 2);
        });
    }
}