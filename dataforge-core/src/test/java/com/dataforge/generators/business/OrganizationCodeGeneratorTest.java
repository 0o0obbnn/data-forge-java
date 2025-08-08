package com.dataforge.generators.business;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class OrganizationCodeGeneratorTest {

    @Test(description = "Should generate a valid 9-digit organization code")
    public void testGenerateValidCode() {
        OrganizationCodeGenerator generator = new OrganizationCodeGenerator(true);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(10, code.length()); // 8 chars + hyphen + 1 check digit
        assertTrue(code.matches("^[A-Z0-9]{8}-[A-Z0-9]$"));

        String body = code.substring(0, 8);
        char expectedCheckDigit = OrganizationCodeGenerator.calculateCheckDigit(body);
        assertEquals(expectedCheckDigit, code.charAt(9));
    }

    @Test(description = "Should generate an invalid 9-digit organization code")
    public void testGenerateInvalidCode() {
        OrganizationCodeGenerator generator = new OrganizationCodeGenerator(false);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(10, code.length());
        assertTrue(code.matches("^[A-Z0-9]{8}-[A-Z0-9]$"));

        String body = code.substring(0, 8);
        char correctCheckDigit = OrganizationCodeGenerator.calculateCheckDigit(body);
        assertNotEquals(correctCheckDigit, code.charAt(9));
    }

    @Test(description = "Should calculate the correct check digit for a known example")
    public void testCalculateCheckDigitKnownValue() {
        // Example from GB 11714-1997 standard
        String body = "10000001";
        char checkDigit = OrganizationCodeGenerator.calculateCheckDigit(body);
        assertEquals(checkDigit, '6');
    }
    
    @Test(description = "Should calculate the correct check digit for another known example")
    public void testCalculateCheckDigitKnownValue2() {
        String body = "74178181";
        char checkDigit = OrganizationCodeGenerator.calculateCheckDigit(body);
        assertEquals(checkDigit, 'X');
    }

    @Test(description = "Should throw exception for invalid body length", expectedExceptions = IllegalArgumentException.class)
    public void testCalculateCheckDigitInvalidLength() {
        OrganizationCodeGenerator.calculateCheckDigit("12345");
    }

    @Test(description = "Should throw exception for invalid characters in body", expectedExceptions = IllegalArgumentException.class)
    public void testCalculateCheckDigitInvalidChars() {
        OrganizationCodeGenerator.calculateCheckDigit("1234567_");
    }
}
