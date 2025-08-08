package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the ReligionGenerator class.
 */
public class ReligionGeneratorTest {

    private final ReligionGenerator generator = new ReligionGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated religion is not null or empty.
     */
    @Test
    public void testGeneratedReligionIsNotEmpty() {
        String religion = generator.generate(context);
        assertNotNull(religion, "Generated religion should not be null.");
        assertFalse(religion.isEmpty(), "Generated religion should not be empty.");
    }

    /**
     * Tests that the generated religion is from the predefined list.
     */
    @Test
    public void testGeneratedReligionIsInPredefinedList() {
        String religion = generator.generate(context);
        assertTrue(Arrays.asList(ReligionGenerator.RELIGIONS).contains(religion),
                   "Generated religion should be one of the predefined values.");
    }
}
