package com.dataforge.generators.text;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.text.SpecialCharGenerator.CharCategory;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SpecialCharGeneratorTest {
    
    @Test
    public void testGenerateDefaultSpecialChars() {
        SpecialCharGenerator generator = new SpecialCharGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateSymbols() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.SYMBOLS, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGeneratePunctuation() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.PUNCTUATION, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateMathematical() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.MATHEMATICAL, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateTechnical() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.TECHNICAL, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateCurrency() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.CURRENCY, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateBrackets() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.BRACKETS, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateQuotes() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.QUOTES, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateAccents() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.ACCENTS, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateMathSymbols() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.MATH_SYMBOLS, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateGreekLetters() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.GREEK_LETTERS, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateCyrillic() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.CYRILLIC, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateArabic() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.ARABIC, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateIdeographic() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.IDEOGRAPHIC, 50, 200, true, true);
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
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.MIXED, 50, 200, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithoutSpaces() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.MIXED, 100, 300, false, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithoutNewlines() {
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.MIXED, 100, 300, true, false);
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
        SpecialCharGenerator generator = new SpecialCharGenerator(CharCategory.MIXED, 1000, 5000, true, true);
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
        SpecialCharGenerator generator = new SpecialCharGenerator();
        assertThat(generator.getName()).isEqualTo("special_char");
    }
    
    @Test
    public void testGetSupportedParameters() {
        SpecialCharGenerator generator = new SpecialCharGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "category", "min_length", "max_length", "include_spaces", "include_newlines"
        );
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMinLength() {
        new SpecialCharGenerator(CharCategory.MIXED, -1, 100, true, true);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMaxLength() {
        new SpecialCharGenerator(CharCategory.MIXED, 100, 50, true, true);
    }
}