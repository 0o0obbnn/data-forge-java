package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class SmsVerificationCodeGeneratorTest {

    @Test(description = "Should generate a code of the default length (6)")
    public void testGenerateDefaultLength() {
        SmsVerificationCodeGenerator generator = new SmsVerificationCodeGenerator();
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("^[0-9]{6}$"));
    }

    @Test(description = "Should generate a code of a specified length (4)")
    public void testGenerateSpecifiedLength() {
        SmsVerificationCodeGenerator generator = new SmsVerificationCodeGenerator(4);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(4, code.length());
        assertTrue(code.matches("^[0-9]{4}$"));
    }

    @Test(description = "Should generate different codes on subsequent calls")
    public void testGeneratesDifferentCodes() {
        SmsVerificationCodeGenerator generator = new SmsVerificationCodeGenerator(8);
        GenerationContext context = new GenerationContext(1);
        String code1 = generator.generate(context);
        String code2 = generator.generate(context);

        assertNotEquals(code1, code2);
    }

    @Test(description = "Should throw exception for zero length", expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        new SmsVerificationCodeGenerator(0);
    }

    @Test(description = "Should throw exception for negative length", expectedExceptions = IllegalArgumentException.class)
    public void testConstructorNegativeLength() {
        new SmsVerificationCodeGenerator(-1);
    }
    
    @Test(description = "Should return the correct length from getLength()")
    public void testGetLength() {
        SmsVerificationCodeGenerator generator = new SmsVerificationCodeGenerator(5);
        assertEquals(5, generator.getLength());
    }
}
