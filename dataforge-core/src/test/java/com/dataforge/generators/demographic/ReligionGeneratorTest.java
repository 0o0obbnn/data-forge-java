package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class ReligionGeneratorTest {

    @Test(description = "Should generate a valid religion")
    public void testGenerate() {
        ReligionGenerator generator = new ReligionGenerator();
        GenerationContext context = new GenerationContext(1);
        String religion = generator.generate(context);
        assertNotNull(religion);
    }

    @Test(description = "Should generate 'None' most of the time with default ratio")
    public void testGenerateDefaultNoneRatio() {
        ReligionGenerator generator = new ReligionGenerator();
        GenerationContext context = new GenerationContext(1);
        int noneCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (generator.generate(context).equals("无")) {
                noneCount++;
            }
        }
        assertTrue(noneCount > 600, "Expected 'None' count to be high");
    }

    @Test(description = "Should generate other religions")
    public void testGenerateOtherReligions() {
        // With a 0.0 noneRatio, it should always be another religion
        ReligionGenerator generator = new ReligionGenerator(0.0);
        GenerationContext context = new GenerationContext(1);
        for (int i = 0; i < 100; i++) {
            String religion = generator.generate(context);
            assertNotEquals("无", religion);
        }
    }

    @Test(description = "Should throw exception for invalid noneRatio", expectedExceptions = IllegalArgumentException.class)
    public void testInvalidNoneRatio() {
        new ReligionGenerator(1.1);
    }
}
