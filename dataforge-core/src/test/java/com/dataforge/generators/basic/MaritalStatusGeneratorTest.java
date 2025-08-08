package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the MaritalStatusGenerator class.
 */
public class MaritalStatusGeneratorTest {

    private final MaritalStatusGenerator generator = new MaritalStatusGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated marital status is not null or empty.
     */
    @Test
    public void testGeneratedMaritalStatusIsNotEmpty() {
        String maritalStatus = generator.generate(context);
        assertNotNull(maritalStatus, "Generated marital status should not be null.");
        assertFalse(maritalStatus.isEmpty(), "Generated marital status should not be empty.");
    }

    /**
     * Tests that the generated marital status is from the predefined list.
     */
    @Test
    public void testGeneratedMaritalStatusIsInPredefinedList() {
        String maritalStatus = generator.generate(context);
        assertTrue(Arrays.asList(MaritalStatusGenerator.MARITAL_STATUSES).contains(maritalStatus),
                   "Generated marital status should be one of the predefined values.");
    }
}
