package com.dataforge.generators.text;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.text.UnicodeBoundaryCharGenerator.UnicodeCategory;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UnicodeBoundaryCharGeneratorTest {
    
    @Test
    public void testGenerateDefaultUnicodeBoundaryText() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateControlCharacters() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.CONTROL, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateSurrogatePairs() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.SURROGATE, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateZeroWidthCharacters() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.ZERO_WIDTH, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateCombiningCharacters() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.COMBINING, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateEmojiCharacters() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.EMOJI, 50, 200, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGeneratePrivateUseCharacters() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.PRIVATE_USE, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateVariationSelectors() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.VARIATION_SELECTORS, 50, 200, false);
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
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.MIXED, 50, 200, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithoutEmoji() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.MIXED, 100, 300, false);
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
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator(UnicodeCategory.MIXED, 1000, 5000, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 5; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGetName() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator();
        assertThat(generator.getName()).isEqualTo("unicode_boundary");
    }
    
    @Test
    public void testGetSupportedParameters() {
        UnicodeBoundaryCharGenerator generator = new UnicodeBoundaryCharGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "category", "min_length", "max_length", "include_emoji"
        );
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMinLength() {
        new UnicodeBoundaryCharGenerator(UnicodeCategory.MIXED, -1, 100, true);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMaxLength() {
        new UnicodeBoundaryCharGenerator(UnicodeCategory.MIXED, 100, 50, true);
    }
}