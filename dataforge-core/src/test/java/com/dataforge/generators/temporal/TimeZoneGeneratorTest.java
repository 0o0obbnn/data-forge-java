package com.dataforge.generators.temporal;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class TimeZoneGeneratorTest {

    @Test(description = "Should generate a valid time zone")
    public void testGenerate() {
        TimeZoneGenerator generator = new TimeZoneGenerator();
        GenerationContext context = new GenerationContext(1);
        String timeZone = generator.generate(context);
        assertNotNull(timeZone);
        assertTrue(timeZone.length() > 2);
    }
}
