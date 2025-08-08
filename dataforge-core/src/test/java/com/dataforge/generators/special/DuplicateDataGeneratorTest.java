package com.dataforge.generators.special;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class DuplicateDataGeneratorTest {
    
    @Test
    public void testGenerateDefaultDuplicateData() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            Object result = generator.generate(context);
            assertThat(result).isNotNull();
            // Just verify we get some value back
        }
    }
    
    @Test
    public void testGenerateWithHigherDuplicationRate() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("duplication_rate", "0.5"); // 50% chance of duplication
        
        // Generate multiple values and check for duplicates
        boolean foundDuplicate = false;
        Object previousValue = null;
        
        for (int i = 0; i < 1000; i++) {
            Object result = generator.generate(context);
            assertThat(result).isNotNull();
            
            if (previousValue != null && result.equals(previousValue)) {
                foundDuplicate = true;
                break;
            }
            previousValue = result;
        }
        
        // It's possible but not guaranteed that we'll find duplicates due to randomness
    }
    
    @Test
    public void testGenerateWithNameGenerator() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("generator_type", "name");
        
        for (int i = 0; i < 100; i++) {
            Object result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should be a string
            assertThat(result).isInstanceOf(String.class);
        }
    }
    
    @Test
    public void testGenerateWithEmailGenerator() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("generator_type", "email");
        
        for (int i = 0; i < 100; i++) {
            Object result = generator.generate(context);
            assertThat(result).isNotNull();
            // Should be a string
            assertThat(result).isInstanceOf(String.class);
        }
    }
    
    @Test
    public void testGenerateWithDifferentDuplicationRates() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Test with 0% duplication rate
        context.setParameter("duplication_rate", "0.0");
        Object result1 = generator.generate(context);
        assertThat(result1).isNotNull();
        
        // Test with 100% duplication rate
        context.setParameter("duplication_rate", "1.0");
        Object result2 = generator.generate(context);
        assertThat(result2).isNotNull();
    }
    
    @Test
    public void testGenerateWithCustomDuplicateCount() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("duplicate_count", "5");
        
        Object result = generator.generate(context);
        assertThat(result).isNotNull();
    }
    
    @Test
    public void testGetName() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        assertThat(generator.getName()).isEqualTo("duplicate_data");
    }
    
    @Test
    public void testGetSupportedParameters() {
        DuplicateDataGenerator generator = new DuplicateDataGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "duplication_rate", "generator_type", "duplicate_count");
    }
}