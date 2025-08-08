package com.dataforge.generators.media;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FileSizeGeneratorTest {
    
    @Test
    public void testGenerateDefaultFileSize() {
        FileSizeGenerator generator = new FileSizeGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains(" ");
            
            // Should be in format "X.XX KB" by default
            String[] parts = result.split(" ");
            assertThat(parts).hasSize(2);
            assertThat(parts[1]).isEqualTo("KB");
            
            // First part should be a number with 2 decimal places
            assertThat(parts[0]).matches("\\d+\\.\\d{2}");
            
            // Value should be between 1 and 100 KB
            double value = Double.parseDouble(parts[0]);
            assertThat(value).isGreaterThanOrEqualTo(1.0);
            assertThat(value).isLessThanOrEqualTo(100.0);
        }
    }
    
    @Test
    public void testGenerateFileSizeInBytes() {
        FileSizeGenerator generator = new FileSizeGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("min", 1024L);
        context.setParameter("max", 2048L);
        context.setParameter("unit", "B");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains(" ");
            
            String[] parts = result.split(" ");
            assertThat(parts).hasSize(2);
            assertThat(parts[1]).isEqualTo("KB"); // Still formatted as KB
            
            // Value should represent 1024-2048 bytes
            double value = Double.parseDouble(parts[0]);
            assertThat(value).isGreaterThanOrEqualTo(1.0);  // 1024 B = 1 KB
            assertThat(value).isLessThanOrEqualTo(2.0);     // 2048 B = 2 KB
        }
    }
    
    @Test
    public void testGenerateFileSizeWithoutUnit() {
        FileSizeGenerator generator = new FileSizeGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("min", 1L);
        context.setParameter("max", 10L);
        context.setParameter("unit", "MB");
        context.setParameter("include_unit", false);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            
            // Should be just a number (bytes)
            long bytes = Long.parseLong(result);
            
            // Should be between 1MB and 10MB in bytes
            assertThat(bytes).isGreaterThanOrEqualTo(1024 * 1024L);      // 1 MB
            assertThat(bytes).isLessThanOrEqualTo(10 * 1024 * 1024L);   // 10 MB
        }
    }
    
    @Test
    public void testGenerateFileSizeInDifferentUnits() {
        FileSizeGenerator generator = new FileSizeGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Test KB
        context.setParameter("min", 1L);
        context.setParameter("max", 10L);
        context.setParameter("unit", "KB");
        String result = generator.generate(context);
        assertThat(result).endsWith("KB");
        
        // Test MB
        context.setParameter("unit", "MB");
        result = generator.generate(context);
        assertThat(result).endsWith("MB");
        
        // Test GB
        context.setParameter("unit", "GB");
        result = generator.generate(context);
        assertThat(result).endsWith("GB");
        
        // Test TB
        context.setParameter("unit", "TB");
        result = generator.generate(context);
        assertThat(result).endsWith("TB");
    }
    
    @Test
    public void testConvertToBytes() {
        FileSizeGenerator generator = new FileSizeGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // This test checks the internal logic by observing the output
        
        // 1 KB = 1024 bytes
        context.setParameter("min", 1L);
        context.setParameter("max", 1L);
        context.setParameter("unit", "KB");
        String result = generator.generate(context);
        String[] parts = result.split(" ");
        double kbValue = Double.parseDouble(parts[0]);
        assertThat(kbValue).isEqualTo(1.0);
        
        // 1 MB = 1024 * 1024 bytes
        context.setParameter("unit", "MB");
        result = generator.generate(context);
        parts = result.split(" ");
        double mbValue = Double.parseDouble(parts[0]);
        assertThat(mbValue).isEqualTo(1.0);
        
        // 1 GB = 1024 * 1024 * 1024 bytes
        context.setParameter("unit", "GB");
        result = generator.generate(context);
        parts = result.split(" ");
        double gbValue = Double.parseDouble(parts[0]);
        assertThat(gbValue).isEqualTo(1.0);
    }
    
    @Test
    public void testFormatFileSize() {
        FileSizeGenerator generator = new FileSizeGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Test B formatting
        context.setParameter("min", 500L);
        context.setParameter("max", 500L);
        context.setParameter("unit", "B");
        context.setParameter("include_unit", false);
        String result = generator.generate(context);
        long bytes = Long.parseLong(result);
        assertThat(bytes).isEqualTo(500L);
        
        // Test automatic unit conversion for large values
        context.setParameter("min", 1099511627776L); // 1TiB in bytes (1024^4)
        context.setParameter("max", 1099511627776L);
        context.setParameter("include_unit", true);
        result = generator.generate(context);
        assertThat(result).endsWith("TB");
    }
    
    @Test
    public void testGetName() {
        FileSizeGenerator generator = new FileSizeGenerator();
        assertThat(generator.getName()).isEqualTo("file_size");
    }
    
    @Test
    public void testGetSupportedParameters() {
        FileSizeGenerator generator = new FileSizeGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder("min", "max", "unit", "include_unit");
    }
}