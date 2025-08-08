package com.dataforge.generators.text;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.text.RichTextGenerator.Format;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RichTextGeneratorTest {
    
    @Test
    public void testGenerateDefaultRichText() {
        RichTextGenerator generator = new RichTextGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateHtmlFormat() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 100, 500, true, true, true, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain HTML tags
            assertThat(result).contains("<", ">");
        }
    }
    
    @Test
    public void testGenerateMarkdownFormat() {
        RichTextGenerator generator = new RichTextGenerator(Format.MARKDOWN, 100, 500, true, true, true, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should contain some Markdown-like syntax or list items
            assertThat(result).satisfiesAnyOf(
                s -> assertThat(s).containsAnyOf("*", "#", "[", "]", "(", ")"),
                s -> assertThat(s).contains("\n- ")
            );
        }
    }
    
    @Test
    public void testGenerateCustomLength() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 50, 200, true, true, true, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Basic validation that we get text back
        }
    }
    
    @Test
    public void testGenerateWithoutHeadings() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 100, 300, false, true, true, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should not contain heading tags
            assertThat(result).doesNotContain("<h1>", "<h2>", "<h3>");
        }
    }
    
    @Test
    public void testGenerateWithoutLists() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 100, 300, true, false, true, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should not contain list tags
            assertThat(result).doesNotContain("<ul>", "<ol>", "<li>");
        }
    }
    
    @Test
    public void testGenerateWithoutLinks() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 100, 300, true, true, false, true, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should not contain link tags
            assertThat(result).doesNotContain("<a href=");
        }
    }
    
    @Test
    public void testGenerateWithoutImages() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 100, 300, true, true, true, false, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should not contain image tags
            assertThat(result).doesNotContain("<img");
        }
    }
    
    @Test
    public void testGenerateWithoutCodeBlocks() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 100, 300, true, true, true, true, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should not contain code block tags
            assertThat(result).doesNotContain("<pre>", "<code>");
        }
    }
    
    @Test
    public void testGenerateVeryLongText() {
        RichTextGenerator generator = new RichTextGenerator(Format.HTML, 1000, 5000, true, true, true, true, true);
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
        RichTextGenerator generator = new RichTextGenerator();
        assertThat(generator.getName()).isEqualTo("richtext");
    }
    
    @Test
    public void testGetSupportedParameters() {
        RichTextGenerator generator = new RichTextGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "format", "min_length", "max_length", 
            "include_headings", "include_lists", "include_links", 
            "include_images", "include_code_blocks"
        );
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMinLength() {
        new RichTextGenerator(Format.HTML, -1, 100, true, true, true, true, true);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidMaxLength() {
        new RichTextGenerator(Format.HTML, 100, 50, true, true, true, true, true);
    }
}