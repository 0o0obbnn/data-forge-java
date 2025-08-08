package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class ZodiacSignGeneratorTest {

    @Test(description = "Should generate a valid zodiac sign")
    public void testGenerate() {
        ZodiacSignGenerator generator = new ZodiacSignGenerator();
        GenerationContext context = new GenerationContext(1);
        String sign = generator.generate(context);
        assertNotNull(sign);
        assertTrue(sign.endsWith("座"));
    }
}
