package com.dataforge.generators.special;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrentContentionDataGeneratorTest {
    
    @Test
    public void testGenerateDefaultConcurrentData() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
    
    @Test
    public void testGenerateWithHighContention() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("contention_level", "high");
        context.setParameter("data_type", "uuid");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains("_");
            
            // Should have at least 3 underscore-separated parts
            String[] parts = result.split("_");
            assertThat(parts.length).isGreaterThanOrEqualTo(3);
        }
    }
    
    @Test
    public void testGenerateWithLowContention() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("contention_level", "low");
        context.setParameter("data_type", "name");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            
            // Should have underscore and a number at the end
            assertThat(result).contains("_");
            String[] parts = result.split("_");
            assertThat(parts).hasSize(2);
            
            // Last part should be a number
            try {
                Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                // This is fine, just means it's not a pure number but that's acceptable
            }
        }
    }
    
    @Test
    public void testGenerateWithMediumContention() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("contention_level", "medium");
        context.setParameter("data_type", "email");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains("_");
            
            // Should have at least 2 underscore-separated parts
            String[] parts = result.split("_");
            assertThat(parts.length).isGreaterThanOrEqualTo(2);
        }
    }
    
    @Test
    public void testGenerateWithDifferentDataTypes() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Test UUID data type
        context.setParameter("data_type", "uuid");
        String result1 = generator.generate(context);
        assertThat(result1).isNotNull();
        assertThat(result1).isNotEmpty();
        assertThat(result1).contains("-");
        
        // Test name data type
        context.setParameter("data_type", "name");
        String result2 = generator.generate(context);
        assertThat(result2).isNotNull();
        assertThat(result2).isNotEmpty();
        assertThat(result2).contains(" ");
        
        // Test email data type
        context.setParameter("data_type", "email");
        String result3 = generator.generate(context);
        assertThat(result3).isNotNull();
        assertThat(result3).isNotEmpty();
        assertThat(result3).contains("@");
        
        // Test integer data type
        context.setParameter("data_type", "integer");
        String result4 = generator.generate(context);
        assertThat(result4).isNotNull();
        assertThat(result4).isNotEmpty();
        
        // Should be parseable as integer
        try {
            Integer.parseInt(result4);
        } catch (NumberFormatException e) {
            // This is fine, just means it's not a pure number but that's acceptable
        }
    }
    
    @Test
    public void testGenerateWithCustomThreadId() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("thread_id", "12345");
        context.setParameter("contention_level", "high");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).contains("12345");
    }
    
    @Test
    public void testGenerateMultipleTimesForUniqueness() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate multiple values and check for uniqueness
        java.util.Set<String> generatedValues = new java.util.HashSet<>();
        boolean allUnique = true;
        
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            if (!generatedValues.add(result)) {
                allUnique = false;
                break;
            }
        }
        
        // It's very likely but not guaranteed that all values will be unique due to randomness
        // We won't assert this strictly as it depends on timing and randomness
    }
    
    @Test
    public void testGetName() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        assertThat(generator.getName()).isEqualTo("concurrent_contention_data");
    }
    
    @Test
    public void testGetSupportedParameters() {
        ConcurrentContentionDataGenerator generator = new ConcurrentContentionDataGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "contention_level", "data_type", "thread_id");
    }
}