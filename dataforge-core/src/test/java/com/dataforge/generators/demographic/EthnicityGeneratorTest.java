package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class EthnicityGeneratorTest {

    @Test(description = "Should generate a valid ethnicity")
    public void testGenerate() {
        EthnicityGenerator generator = new EthnicityGenerator();
        GenerationContext context = new GenerationContext(1);
        String ethnicity = generator.generate(context);
        assertNotNull(ethnicity);
    }

    @Test(description = "Should generate Han ethnicity most of the time with default ratio")
    public void testGenerateDefaultHanRatio() {
        EthnicityGenerator generator = new EthnicityGenerator();
        GenerationContext context = new GenerationContext(1);
        int hanCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (generator.generate(context).equals("汉族")) {
                hanCount++;
            }
        }
        assertTrue(hanCount > 850, "Expected Han count to be high");
    }

    @Test(description = "Should generate minority ethnicities")
    public void testGenerateMinority() {
        // With a 0.0 hanRatio, it should always be a minority
        EthnicityGenerator generator = new EthnicityGenerator(0.0);
        GenerationContext context = new GenerationContext(1);
        for (int i = 0; i < 100; i++) {
            String ethnicity = generator.generate(context);
            assertNotEquals("汉族", ethnicity);
        }
    }

    @Test(description = "Should throw exception for invalid hanRatio", expectedExceptions = IllegalArgumentException.class)
    public void testInvalidHanRatio() {
        new EthnicityGenerator(1.1);
    }
}
