package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the OccupationGenerator class.
 */
public class OccupationGeneratorTest {

    private final OccupationGenerator generator = new OccupationGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated occupation is not null or empty.
     */
    @Test
    public void testGeneratedOccupationIsNotEmpty() {
        String occupation = generator.generate(context);
        assertNotNull(occupation, "Generated occupation should not be null.");
        assertFalse(occupation.isEmpty(), "Generated occupation should not be empty.");
    }

    /**
     * Tests that the generated occupation is from the predefined list.
     */
    @Test
    public void testGeneratedOccupationIsInPredefinedList() {
        String occupation = generator.generate(context);
        assertTrue(Arrays.asList(OccupationGenerator.OCCUPATIONS).contains(occupation),
                   "Generated occupation should be one of the predefined values.");
    }
}