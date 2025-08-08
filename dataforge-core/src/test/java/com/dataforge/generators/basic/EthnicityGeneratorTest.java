package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the EthnicityGenerator class.
 */
public class EthnicityGeneratorTest {

    private final EthnicityGenerator generator = new EthnicityGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated ethnicity is not null or empty.
     */
    @Test
    public void testGeneratedEthnicityIsNotEmpty() {
        String ethnicity = generator.generate(context);
        assertNotNull(ethnicity, "Generated ethnicity should not be null.");
        assertFalse(ethnicity.isEmpty(), "Generated ethnicity should not be empty.");
    }

    /**
     * Tests that the generated ethnicity is from the predefined list.
     */
    @Test
    public void testGeneratedEthnicityIsInPredefinedList() {
        String ethnicity = generator.generate(context);
        assertTrue(Arrays.asList(EthnicityGenerator.ETHNICITIES).contains(ethnicity),
                   "Generated ethnicity should be one of the predefined values.");
    }
}
