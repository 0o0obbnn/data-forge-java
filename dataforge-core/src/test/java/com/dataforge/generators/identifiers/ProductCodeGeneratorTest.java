package com.dataforge.generators.identifiers;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the ProductCodeGenerator class.
 */
public class ProductCodeGeneratorTest {

    private final ProductCodeGenerator generator = new ProductCodeGenerator();

    @Test
    public void testGenerateSku() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "SKU");
        context.setParameter("length", 10);
        String sku = generator.generate(context);
        assertNotNull(sku);
        assertEquals(sku.length(), 10);
    }

    @Test
    public void testGenerateGtin() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "GTIN");
        String gtin = generator.generate(context);
        assertNotNull(gtin);
        assertEquals(gtin.length(), 13);
        assertTrue(isValidEan13(gtin));
    }

    @Test
    public void testGenerateIsbn() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "ISBN");
        String isbn = generator.generate(context);
        assertNotNull(isbn);
        assertEquals(isbn.length(), 13);
        assertTrue(isbn.startsWith("978"));
        assertTrue(isValidEan13(isbn));
    }

    @Test
    public void testGenerateIssn() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "ISSN");
        String issn = generator.generate(context);
        assertNotNull(issn);
        assertEquals(issn.length(), 8);
        assertTrue(isValidIssn(issn));
    }

    private boolean isValidEan13(String number) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(number.charAt(12));
    }

    private boolean isValidIssn(String issn) {
        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(issn.charAt(i)) * (8 - i);
        }
        int remainder = sum % 11;
        char checkDigit = (remainder == 0) ? '0' : ((11 - remainder) == 10) ? 'X' : (char) ((11 - remainder) + '0');
        return checkDigit == issn.charAt(7);
    }
}