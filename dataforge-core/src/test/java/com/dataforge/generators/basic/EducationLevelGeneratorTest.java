package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the EducationLevelGenerator class.
 */
public class EducationLevelGeneratorTest {

    private final EducationLevelGenerator generator = new EducationLevelGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated education level is not null or empty.
     */
    @Test
    public void testGeneratedEducationLevelIsNotEmpty() {
        String educationLevel = generator.generate(context);
        assertNotNull(educationLevel, "Generated education level should not be null.");
        assertFalse(educationLevel.isEmpty(), "Generated education level should not be empty.");
    }

    /**
     * Tests that the generated education level is from the predefined list.
     */
    @Test
    public void testGeneratedEducationLevelIsInPredefinedList() {
        String educationLevel = generator.generate(context);
        assertTrue(Arrays.asList(EducationLevelGenerator.EDUCATION_LEVELS).contains(educationLevel),
                   "Generated education level should be one of the predefined values.");
    }
}
