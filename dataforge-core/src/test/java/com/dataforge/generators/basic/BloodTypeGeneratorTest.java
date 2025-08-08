package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the BloodTypeGenerator class.
 */
public class BloodTypeGeneratorTest {

    private final BloodTypeGenerator generator = new BloodTypeGenerator();
    private final GenerationContext context = new GenerationContext(1);

    /**
     * Tests that the generated blood type is not null or empty.
     */
    @Test
    public void testGeneratedBloodTypeIsNotEmpty() {
        String bloodType = generator.generate(context);
        assertNotNull(bloodType, "Generated blood type should not be null.");
        assertFalse(bloodType.isEmpty(), "Generated blood type should not be empty.");
    }

    /**
     * Tests that the generated blood type is from the predefined list.
     */
    @Test
    public void testGeneratedBloodTypeIsInPredefinedList() {
        String bloodType = generator.generate(context);
        assertTrue(Arrays.asList(BloodTypeGenerator.BLOOD_TYPES).contains(bloodType),
                   "Generated blood type should be one of the predefined values.");
    }
}
