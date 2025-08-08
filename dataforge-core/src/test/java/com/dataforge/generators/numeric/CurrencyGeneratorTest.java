package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CurrencyGeneratorTest {
    
    @Test
    public void testCurrencyGeneration() {
        CurrencyGenerator generator = new CurrencyGenerator();
        GenerationContext context = new GenerationContext(100); // Generate 100 values
        
        for (int i = 0; i < 100; i++) {
            String currency = generator.generate(context);
            assertNotNull(currency);
            assertFalse(currency.isEmpty());
            assertTrue(currency.matches("[A-Z]{3}")); // ISO 4217 currency codes are 3 uppercase letters
        }
    }
}