package com.dataforge.generators.communication;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the LandlinePhoneGenerator class.
 */
public class LandlinePhoneGeneratorTest {

    private final LandlinePhoneGenerator generator = new LandlinePhoneGenerator();

    @Test
    public void testGenerateDefault() {
        GenerationContext context = new GenerationContext(1);
        String phoneNumber = generator.generate(context);
        assertNotNull(phoneNumber);
        assertTrue(phoneNumber.matches("\\d{3,4}-\\d{8}"));
    }

    @Test
    public void testGenerateWithExtension() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("extension_length", 4);
        String phoneNumber = generator.generate(context);
        assertNotNull(phoneNumber);
        assertTrue(phoneNumber.matches("\\d{3,4}-\\d{8}-\\d{4}"));
    }
}
