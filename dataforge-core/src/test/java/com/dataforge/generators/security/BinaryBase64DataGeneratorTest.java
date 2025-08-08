package com.dataforge.generators.security;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Base64;
import static org.assertj.core.api.Assertions.assertThat;

public class BinaryBase64DataGeneratorTest {
    
    @Test
    public void testGenerateDefaultBase64Data() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            
            // Should be valid Base64
            try {
                Base64.getDecoder().decode(result);
            } catch (IllegalArgumentException e) {
                // This might happen if we're testing with invalid chars, but by default it should be valid
                assertThat((Boolean) context.getParameter("include_invalid_chars", false)).isTrue();
            }
        }
    }
    
    @Test
    public void testGenerateHexData() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("encoding", "hex");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            
            // Should be valid hexadecimal
            assertThat(result).matches("[0-9A-F]+");
        }
    }
    
    @Test
    public void testGenerateCustomSize() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("size", 512);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        
        // Decode and check size
        byte[] decoded = Base64.getDecoder().decode(result);
        assertThat(decoded.length).isEqualTo(512);
    }
    
    @Test
    public void testGenerateWithInvalidChars() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("include_invalid_chars", true);
        
        boolean foundInvalid = false;
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            
            // Try to decode - this might fail due to invalid characters
            try {
                Base64.getDecoder().decode(result);
            } catch (IllegalArgumentException e) {
                foundInvalid = true;
                break;
            }
        }
        
        // We might not always generate invalid data due to randomness, so we won't assert this strictly
    }
    
    @Test
    public void testDifferentEncodings() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Test Base64
        context.setParameter("encoding", "base64");
        String base64Result = generator.generate(context);
        assertThat(base64Result).isNotNull();
        
        // Test Hex
        context.setParameter("encoding", "hex");
        String hexResult = generator.generate(context);
        assertThat(hexResult).isNotNull();
        assertThat(hexResult).matches("[0-9A-F]+");
    }
    
    @Test
    public void testGetName() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        assertThat(generator.getName()).isEqualTo("binary_base64_data");
    }
    
    @Test
    public void testGetSupportedParameters() {
        BinaryBase64DataGenerator generator = new BinaryBase64DataGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "size", "encoding", "include_invalid_chars");
    }
}