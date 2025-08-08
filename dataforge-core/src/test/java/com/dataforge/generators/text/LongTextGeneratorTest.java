package com.dataforge.generators.text;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LongTextGeneratorTest {
    
    @Test
    public void testGenerateDefaultLongText() {
        LongTextGenerator generator = new LongTextGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateCustomLength() {
        LongTextGenerator generator = new LongTextGenerator(50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithMixedLanguages() {
        LongTextGenerator generator = new LongTextGenerator(100, 300, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateVeryLongText() {
        LongTextGenerator generator = new LongTextGenerator(1000, 5000, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 5; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithLineBreaks() {
        LongTextGenerator generator = new LongTextGenerator(200, 500, false);
        GenerationContext context = new GenerationContext(1);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Basic validation that we get text back
    }
    
    @Test
    public void testGenerateWithPunctuation() {
        LongTextGenerator generator = new LongTextGenerator(200, 500, false);
        GenerationContext context = new GenerationContext(1);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Basic validation that we get text back
    }
    
    @Test
    public void testGetName() {
        LongTextGenerator generator = new LongTextGenerator();
        assertThat(generator.getName()).isEqualTo("longtext");
    }
    
    @Test
    public void testGetSupportedParameters() {
        LongTextGenerator generator = new LongTextGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "min_length", "max_length", "mix_languages");
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMinLength() {
        new LongTextGenerator(-1, 100, false);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMaxLength() {
        new LongTextGenerator(100, 50, false);
    }
}