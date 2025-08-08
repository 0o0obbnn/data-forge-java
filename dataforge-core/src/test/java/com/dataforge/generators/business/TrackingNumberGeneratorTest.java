package com.dataforge.generators.business;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class TrackingNumberGeneratorTest {

    @Test(description = "Should generate a tracking number of the default length")
    public void testGenerateDefault() {
        TrackingNumberGenerator generator = new TrackingNumberGenerator();
        GenerationContext context = new GenerationContext(1);
        String number = generator.generate(context);
        assertNotNull(number);
        assertEquals(12, number.length());
        assertTrue(number.matches("^[0-9]{12}$"));
    }

    @Test(description = "Should generate a tracking number with a specified length and prefix")
    public void testGenerateWithPrefix() {
        TrackingNumberGenerator generator = new TrackingNumberGenerator(10, "TN");
        GenerationContext context = new GenerationContext(1);
        String number = generator.generate(context);
        assertNotNull(number);
        assertEquals(12, number.length());
        assertTrue(number.startsWith("TN"));
        assertTrue(number.matches("^TN[0-9]{10}$"));
    }

    @Test(description = "Should throw exception for zero length", expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        new TrackingNumberGenerator(0, "");
    }
}
