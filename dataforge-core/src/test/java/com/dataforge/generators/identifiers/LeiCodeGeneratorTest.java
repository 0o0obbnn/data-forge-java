package com.dataforge.generators.identifiers;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the LeiCodeGenerator class.
 */
public class LeiCodeGeneratorTest {

    private final LeiCodeGenerator generator = new LeiCodeGenerator();

    @Test
    public void testGenerateValidCode() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("valid", true);
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(code.length(), 20);
        assertTrue(isValid(code));
    }

    @Test
    public void testGenerateInvalidCode() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("valid", false);
        String code = generator.generate(context);
        assertNotNull(code);
        assertEquals(code.length(), 20);
        assertFalse(isValid(code));
    }

    private boolean isValid(String lei) {
        if (lei == null || lei.length() != 20) {
            return false;
        }
        String numericLei = toNumericString(lei);
        try {
            java.math.BigInteger num = new java.math.BigInteger(numericLei);
            return num.mod(java.math.BigInteger.valueOf(97)).intValue() == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String toNumericString(String str) {
        StringBuilder numeric = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                numeric.append(c);
            } else {
                numeric.append(Character.getNumericValue(c));
            }
        }
        return numeric.toString();
    }
}
