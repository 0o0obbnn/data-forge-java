package com.dataforge.generators.identifiers;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the OrganizationCodeGenerator class.
 */
public class OrganizationCodeGeneratorTest {

    private final OrganizationCodeGenerator generator = new OrganizationCodeGenerator();

    @Test
    public void testGenerateValidCode() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("valid", true);
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(code.length(), 10);
        assertTrue(code.matches("[0-9A-Z]{8}-[0-9X]"));
        assertTrue(isValid(code));
    }

    @Test
    public void testGenerateInvalidCode() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("valid", false);
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(code.length(), 10);
        assertFalse(isValid(code));
    }

    private boolean isValid(String code) {
        if (code == null || !code.matches("[0-9A-Z]{8}-[0-9X]")) {
            return false;
        }
        String codePart = code.substring(0, 8);
        char checkDigit = code.charAt(9);
        return calculateCheckDigit(codePart) == checkDigit;
    }

    private char calculateCheckDigit(String code) {
        int[] weights = {3, 7, 9, 10, 5, 8, 4, 2};
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            char c = code.charAt(i);
            int value = Character.getNumericValue(c);
            sum += value * weights[i];
        }

        int remainder = sum % 11;
        int checkValue = 11 - remainder;

        if (checkValue == 10) {
            return 'X';
        } else if (checkValue == 11) {
            return '0';
        } else {
            return (char) ('0' + checkValue);
        }
    }
}
