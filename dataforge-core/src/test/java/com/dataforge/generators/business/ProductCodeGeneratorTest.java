package com.dataforge.generators.business;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class ProductCodeGeneratorTest {

    @Test(description = "Should generate a valid EAN-13 code")
    public void testGenerateValidEan13() {
        ProductCodeGenerator generator = new ProductCodeGenerator(ProductCodeGenerator.ProductCodeType.EAN13, true);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(13, code.length());
        assertTrue(code.matches("^[0-9]{13}$"));

        String body = code.substring(0, 12);
        char expectedCheckDigit = ProductCodeGenerator.calculateEan13CheckDigit(body);
        assertEquals(expectedCheckDigit, code.charAt(12));
    }

    @Test(description = "Should generate an invalid EAN-13 code")
    public void testGenerateInvalidEan13() {
        ProductCodeGenerator generator = new ProductCodeGenerator(ProductCodeGenerator.ProductCodeType.EAN13, false);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(13, code.length());
        assertTrue(code.matches("^[0-9]{13}$"));

        String body = code.substring(0, 12);
        char correctCheckDigit = ProductCodeGenerator.calculateEan13CheckDigit(body);
        assertNotEquals(correctCheckDigit, code.charAt(12));
    }

    @Test(description = "Should calculate the correct check digit for a known EAN-13 example")
    public void testCalculateEan13CheckDigitKnownValue() {
        String body = "400638133393";
        char checkDigit = ProductCodeGenerator.calculateEan13CheckDigit(body);
        assertEquals('1', checkDigit);
    }

    @Test(description = "Should throw exception for invalid EAN-13 body length", expectedExceptions = IllegalArgumentException.class)
    public void testCalculateEan13CheckDigitInvalidLength() {
        ProductCodeGenerator.calculateEan13CheckDigit("12345");
    }
    
    @Test(description = "Should generate a valid UPC-A code")
    public void testGenerateValidUpca() {
        ProductCodeGenerator generator = new ProductCodeGenerator(ProductCodeGenerator.ProductCodeType.UPCA, true);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(12, code.length());
        assertTrue(code.matches("^[0-9]{12}$"));

        String body = code.substring(0, 11);
        char expectedCheckDigit = ProductCodeGenerator.calculateUpcaCheckDigit(body);
        assertEquals(expectedCheckDigit, code.charAt(11));
    }

    @Test(description = "Should generate a valid ISBN-13 code")
    public void testGenerateValidIsbn13() {
        ProductCodeGenerator generator = new ProductCodeGenerator(ProductCodeGenerator.ProductCodeType.ISBN13, true);
        GenerationContext context = new GenerationContext(1);
        String code = generator.generate(context);

        assertNotNull(code);
        assertEquals(13, code.length());
        assertTrue(code.matches("^(978|979)[0-9]{10}$"));

        String body = code.substring(0, 12);
        char expectedCheckDigit = ProductCodeGenerator.calculateEan13CheckDigit(body);
        assertEquals(expectedCheckDigit, code.charAt(12));
    }

    @Test(description = "Should calculate the correct check digit for a known UPC-A example")
    public void testCalculateUpcaCheckDigitKnownValue() {
        String body = "03600029145";
        char checkDigit = ProductCodeGenerator.calculateUpcaCheckDigit(body);
        assertEquals('2', checkDigit);
    }
}
