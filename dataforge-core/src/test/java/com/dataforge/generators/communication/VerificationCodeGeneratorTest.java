package com.dataforge.generators.communication;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the VerificationCodeGenerator class.
 */
public class VerificationCodeGeneratorTest {

    private final VerificationCodeGenerator generator = new VerificationCodeGenerator();

    @Test
    public void testGenerateNumericCode() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("length", 6);
        context.setParameter("chars", "NUMERIC");
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(code.length(), 6);
        assertTrue(code.matches("[0-9]+"));
    }

    @Test
    public void testGenerateAlphaNumericCode() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("length", 8);
        context.setParameter("chars", "ALPHANUMERIC");
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(code.length(), 8);
        assertTrue(code.matches("[a-zA-Z0-9]+"));
    }
}
