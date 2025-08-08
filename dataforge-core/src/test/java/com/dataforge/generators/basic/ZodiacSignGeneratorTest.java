package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the ZodiacSignGenerator class.
 */
public class ZodiacSignGeneratorTest {

    private final ZodiacSignGenerator generator = new ZodiacSignGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated zodiac sign is not null or empty.
     */
    @Test
    public void testGeneratedZodiacSignIsNotEmpty() {
        String zodiacSign = generator.generate(context);
        assertNotNull(zodiacSign, "Generated zodiac sign should not be null.");
        assertFalse(zodiacSign.isEmpty(), "Generated zodiac sign should not be empty.");
    }

    /**
     * Tests that the generated zodiac sign is from the predefined list.
     */
    @Test
    public void testGeneratedZodiacSignIsInPredefinedList() {
        String zodiacSign = generator.generate(context);
        assertTrue(Arrays.asList(ZodiacSignGenerator.ZODIAC_SIGNS).contains(zodiacSign),
                   "Generated zodiac sign should be one of the predefined values.");
    }
}
