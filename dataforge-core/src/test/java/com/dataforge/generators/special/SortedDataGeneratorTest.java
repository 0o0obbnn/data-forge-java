package com.dataforge.generators.special;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SortedDataGeneratorTest {
    
    @Test
    public void testGenerateDefaultSortedData() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            java.util.List<Object> result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(10);
        }
    }
    
    @Test
    public void testGenerateAscendingOrder() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("sort_order", "asc");
        context.setParameter("data_type", "integer");
        context.setParameter("count", 20);
        
        for (int i = 0; i < 100; i++) {
            java.util.List<Object> result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(20);
            
            // Check if sorted in ascending order
            for (int j = 0; j < result.size() - 1; j++) {
                Comparable<Object> current = (Comparable<Object>) result.get(j);
                Comparable<Object> next = (Comparable<Object>) result.get(j + 1);
                assertThat(current.compareTo(next)).isLessThanOrEqualTo(0);
            }
        }
    }
    
    @Test
    public void testGenerateDescendingOrder() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("sort_order", "desc");
        context.setParameter("data_type", "integer");
        context.setParameter("count", 15);
        
        for (int i = 0; i < 100; i++) {
            java.util.List<Object> result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(15);
            
            // Check if sorted in descending order
            for (int j = 0; j < result.size() - 1; j++) {
                Comparable<Object> current = (Comparable<Object>) result.get(j);
                Comparable<Object> next = (Comparable<Object>) result.get(j + 1);
                assertThat(current.compareTo(next)).isGreaterThanOrEqualTo(0);
            }
        }
    }
    
    @Test
    public void testGenerateCustomOrder() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("sort_order", "custom");
        context.setParameter("data_type", "string");
        context.setParameter("count", 12);
        
        for (int i = 0; i < 100; i++) {
            java.util.List<Object> result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(12);
            // Just verify we get a list back - custom order means shuffled
        }
    }
    
    @Test
    public void testGenerateWithUniqueValues() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("unique", true);
        context.setParameter("data_type", "integer");
        context.setParameter("count", 25);
        
        for (int i = 0; i < 100; i++) {
            java.util.List<Object> result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(25);
            
            // Check for uniqueness
            java.util.Set<Object> uniqueSet = new java.util.HashSet<>(result);
            assertThat(uniqueSet).hasSameSizeAs(result);
        }
    }
    
    @Test
    public void testGenerateWithStringDataType() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("data_type", "string");
        context.setParameter("count", 8);
        
        for (int i = 0; i < 100; i++) {
            java.util.List<Object> result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(8);
            
            // Check if all elements are strings
            for (Object obj : result) {
                assertThat(obj).isInstanceOf(String.class);
            }
            
            // Check if sorted in ascending order
            for (int j = 0; j < result.size() - 1; j++) {
                String current = (String) result.get(j);
                String next = (String) result.get(j + 1);
                assertThat(current.compareTo(next)).isLessThanOrEqualTo(0);
            }
        }
    }
    
    @Test
    public void testGenerateWithDifferentCounts() {
        SortedDataGenerator generator = new SortedDataGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Test with small count
        context.setParameter("count", 3);
        java.util.List<Object> result1 = generator.generate(context);
        assertThat(result1).isNotNull();
        assertThat(result1).hasSize(3);
        
        // Test with large count
        context.setParameter("count", 100);
        java.util.List<Object> result2 = generator.generate(context);
        assertThat(result2).isNotNull();
        assertThat(result2).hasSize(100);
    }
    
    @Test
    public void testGetName() {
        SortedDataGenerator generator = new SortedDataGenerator();
        assertThat(generator.getName()).isEqualTo("sorted_data");
    }
    
    @Test
    public void testGetSupportedParameters() {
        SortedDataGenerator generator = new SortedDataGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "count", "sort_order", "data_type", "unique");
    }
}