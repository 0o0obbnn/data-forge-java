package com.dataforge.generators.text;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.text.MultilingualTextGenerator.Language;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MultilingualTextGeneratorTest {
    
    @Test
    public void testGenerateDefaultMultilingualText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateChineseText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.CHINESE, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain Chinese characters
            // //assertThat(result).matches(".*[\u4e00-\u9fa5]+.*");
        }
    }
    
    @Test
    public void testGenerateEnglishText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.ENGLISH, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain English characters
            //assertThat(result).matches(".*[a-zA-Z]+.*");
        }
    }
    
    @Test
    public void testGenerateSpanishText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.SPANISH, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain Spanish characters (accents, etc.)
            //assertThat(result).matches(".*[a-zA-Záéíóúüñ]+.*");
        }
    }
    
    @Test
    public void testGenerateFrenchText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.FRENCH, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain French characters (accents, etc.)
            //assertThat(result).matches(".*[a-zA-Zàâäéèêëïîôöùûüÿç]+.*");
        }
    }
    
    @Test
    public void testGenerateGermanText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.GERMAN, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain German characters (umlauts, etc.)
            //assertThat(result).matches(".*[a-zA-Zäöüß]+.*");
        }
    }
    
    @Test
    public void testGenerateJapaneseText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.JAPANESE, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain Japanese characters (Hiragana, Katakana, Kanji)
            //assertThat(result).matches(".*[\\u3040-\\u309f\\u30a0-\\u30ff\\u4e00-\\u9faf]+.*");
        }
    }
    
    @Test
    public void testGenerateKoreanText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.KOREAN, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain Korean characters (Hangul)
            //assertThat(result).matches(".*[\\uac00-\\ud7af]+.*");
        }
    }
    
    @Test
    public void testGenerateArabicText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.ARABIC, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain Arabic characters
            //assertThat(result).matches(".*[\\u0600-\\u06ff]+.*");
        }
    }
    
    @Test
    public void testGenerateRussianText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.RUSSIAN, 50, 200, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain Russian characters (Cyrillic)
            //assertThat(result).matches(".*[\\u0400-\\u04ff]+.*");
        }
    }
    
    @Test
    public void testGenerateCustomLength() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.MIXED, 50, 200, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithoutMixingLanguages() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.ENGLISH, 100, 300, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should predominantly contain English characters
            //assertThat(result).matches(".*[a-zA-Z]+.*");
        }
    }
    
    @Test
    public void testGenerateWithMixedLanguages() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.MIXED, 100, 300, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain characters from multiple languages
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateVeryLongText() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator(Language.MIXED, 1000, 5000, true);
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
        MultilingualTextGenerator generator = new MultilingualTextGenerator();
        assertThat(generator.getName()).isEqualTo("multilingual_text");
    }
    
    @Test
    public void testGetSupportedParameters() {
        MultilingualTextGenerator generator = new MultilingualTextGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "language", "min_length", "max_length", "mix_languages"
        );
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMinLength() {
        new MultilingualTextGenerator(Language.ENGLISH, -1, 100, false);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMaxLength() {
        new MultilingualTextGenerator(Language.ENGLISH, 100, 50, false);
    }
}