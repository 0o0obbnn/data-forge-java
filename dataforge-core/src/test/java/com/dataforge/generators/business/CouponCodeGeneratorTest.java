package com.dataforge.generators.business;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class CouponCodeGeneratorTest {

    @Test(description = "Should generate a coupon code of the default length")
    public void testGenerateDefault() {
        CouponCodeGenerator generator = new CouponCodeGenerator();
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(8, code.length());
        assertTrue(code.matches("^[A-Z0-9]{8}$"));
    }

    @Test(description = "Should generate a coupon code with a specified length and prefix")
    public void testGenerateWithPrefix() {
        CouponCodeGenerator generator = new CouponCodeGenerator(10, "SALE-");
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(15, code.length());
        assertTrue(code.startsWith("SALE-"));
        assertTrue(code.matches("^SALE-[A-Z0-9]{10}$"));
    }

    @Test(description = "Should throw exception for zero length", expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        new CouponCodeGenerator(0, "");
    }
}
