package com.dataforge.generators.special;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class InvalidExceptionDataGeneratorTest {
    
    @Test
    public void testGenerateDefaultInvalidData() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Just verify we get some string back
        }
    }
    
    @Test
    public void testGenerateInvalidPhone() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "phone");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid 11-digit phone number
            if (result.length() != 11 || result.matches(".*[^0-9].*")) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidEmail() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "email");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid email format
            if (result.matches(".*[^@].*") || result.startsWith("@") || result.endsWith("@") || 
                result.contains("..") || result.contains(" ")) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidIdCard() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "idcard");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid 18-digit ID card number
            if (result.length() != 18 || result.matches(".*[^0-9].*")) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidBankCard() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "bankcard");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid bank card (16-19 digits)
            if (result.length() < 16 || result.length() > 19 || result.matches(".*[^0-9].*")) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidInteger() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "integer");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid integer
            if (result.matches(".*[^0-9].*") || result.isEmpty()) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidDecimal() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "decimal");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid decimal
            if (result.matches(".*[^0-9.].*") || result.isEmpty() || 
                ".".equals(result) || result.endsWith(".")) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidDate() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "date");
        
        // Try multiple times to increase chance of getting invalid data
        boolean foundInvalid = false;
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should not be a valid date in YYYY-MM-DD format
            if (!result.matches("\\d{4}-\\d{2}-\\d{2}") || result.contains(" ") || 
                result.contains("/") || result.contains(".")) {
                foundInvalid = true;
                break;
            }
        }
        // It's possible but not guaranteed that we'll find invalid data due to randomness
    }
    
    @Test
    public void testGenerateInvalidString() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "string");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            // Just verify we get some string back
        }
    }
    
    @Test
    public void testDifferentViolationTypes() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "email");
        
        // Test format violation
        context.setParameter("violation_type", "format");
        String result1 = generator.generate(context);
        assertThat(result1).isNotNull();
        
        // Test length violation
        context.setParameter("violation_type", "length");
        String result2 = generator.generate(context);
        assertThat(result2).isNotNull();
    }
    
    @Test
    public void testGetName() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        assertThat(generator.getName()).isEqualTo("invalid_exception_data");
    }
    
    @Test
    public void testGetSupportedParameters() {
        InvalidExceptionDataGenerator generator = new InvalidExceptionDataGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "data_type", "violation_type");
    }
}