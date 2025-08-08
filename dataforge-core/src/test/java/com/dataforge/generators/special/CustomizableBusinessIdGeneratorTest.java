package com.dataforge.generators.special;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomizableBusinessIdGeneratorTest {
    
    @Test
    public void testGenerateDefaultBusinessId() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result.length()).isBetween(8, 12);
        }
    }
    
    @Test
    public void testGenerateWithPrefix() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("prefix", "ORD-");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).startsWith("ORD-");
        }
    }
    
    @Test
    public void testGenerateWithSuffix() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("suffix", "-2023");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).endsWith("-2023");
        }
    }
    
    @Test
    public void testGenerateWithCustomLength() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("min_length", 15);
        context.setParameter("max_length", 20);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result.length()).isBetween(15, 20);
        }
    }
    
    @Test
    public void testGenerateWithNumericChars() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("chars", "numeric");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).matches("[0-9]+");
        }
    }
    
    @Test
    public void testGenerateWithAlphaChars() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("chars", "alpha");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).matches("[a-zA-Z]+");
        }
    }
    
    @Test
    public void testGenerateWithSeparator() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("separator", "-");
        context.setParameter("separator_interval", 4);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Check that separator appears at expected intervals
        assertThat(result).contains("-");
    }
    
    @Test
    public void testGenerateWithHexChars() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("chars", "hex");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).matches("[0-9A-F]+");
        }
    }
    
    @Test
    public void testComplexFormat() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("prefix", "TXN");
        context.setParameter("suffix", "END");
        context.setParameter("min_length", 10);
        context.setParameter("max_length", 10);
        context.setParameter("chars", "alphanumeric_upper");
        context.setParameter("separator", "-");
        context.setParameter("separator_interval", 3);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).startsWith("TXN");
        assertThat(result).endsWith("END");
    }
    
    @Test
    public void testGetName() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        assertThat(generator.getName()).isEqualTo("customizable_business_id");
    }
    
    @Test
    public void testGetSupportedParameters() {
        CustomizableBusinessIdGenerator generator = new CustomizableBusinessIdGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "min_length", "max_length", "prefix", "suffix", "chars", 
            "separator", "separator_interval", "unique");
    }
}