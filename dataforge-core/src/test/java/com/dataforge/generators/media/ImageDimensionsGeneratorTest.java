package com.dataforge.generators.media;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ImageDimensionsGeneratorTest {
    
    @Test
    public void testGenerateDefaultDimensions() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains("x");
            
            String[] parts = result.split("x");
            assertThat(parts).hasSize(2);
            
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);
            
            // Check default ranges
            assertThat(width).isGreaterThanOrEqualTo(100);
            assertThat(width).isLessThanOrEqualTo(1920);
            assertThat(height).isGreaterThanOrEqualTo(100);
            assertThat(height).isLessThanOrEqualTo(1080);
        }
    }
    
    @Test
    public void testGenerateCustomDimensions() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("min_width", 500);
        context.setParameter("max_width", 1500);
        context.setParameter("min_height", 300);
        context.setParameter("max_height", 1200);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains("x");
            
            String[] parts = result.split("x");
            assertThat(parts).hasSize(2);
            
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);
            
            // Check custom ranges
            assertThat(width).isGreaterThanOrEqualTo(500);
            assertThat(width).isLessThanOrEqualTo(1500);
            assertThat(height).isGreaterThanOrEqualTo(300);
            assertThat(height).isLessThanOrEqualTo(1200);
        }
    }
    
    @Test
    public void testGenerateWithAspectRatio() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("maintain_aspect_ratio", true);
        context.setParameter("aspect_ratio", "1.5"); // 3:2 aspect ratio
        // Set fixed dimensions to make the test more predictable
        context.setParameter("min_width", 1500);
        context.setParameter("max_width", 1500);
        context.setParameter("min_height", 100);
        context.setParameter("max_height", 1500);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).contains("x");
        
        String[] parts = result.split("x");
        assertThat(parts).hasSize(2);
        
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);
        
        // Check if aspect ratio is maintained (with some tolerance for rounding)
        float calculatedRatio = (float) width / height;
        assertThat(calculatedRatio).isCloseTo(1.5f, within(0.01f));
    }
    
    @Test
    public void testGenerateWithCustomFormat() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "Width: {width}, Height: {height}");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains("Width:");
            assertThat(result).contains("Height:");
            assertThat(result).contains(",");
            
            // Extract values
            String[] parts = result.split("[,:]");
            assertThat(parts).hasSize(4);
            
            int width = Integer.parseInt(parts[1].trim());
            int height = Integer.parseInt(parts[3].trim());
            
            // Check ranges
            assertThat(width).isGreaterThanOrEqualTo(100);
            assertThat(width).isLessThanOrEqualTo(1920);
            assertThat(height).isGreaterThanOrEqualTo(100);
            assertThat(height).isLessThanOrEqualTo(1080);
        }
    }
    
    @Test
    public void testGenerateWithDifferentAspectRatioValues() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        GenerationContext context = new GenerationContext(1, 12345L); // Add a fixed seed
        context.setParameter("maintain_aspect_ratio", true);
        
        // Test 16:9 aspect ratio
        context.setParameter("aspect_ratio", "1.777");
        context.setParameter("min_width", 1280);
        context.setParameter("max_width", 1280);
        String result1 = generator.generate(context);
        String[] parts1 = result1.split("x");
        int width1 = Integer.parseInt(parts1[0]);
        int height1 = Integer.parseInt(parts1[1]);
        float ratio1 = (float) width1 / height1;
        assertThat(ratio1).isCloseTo(1.777f, within(0.02f));
        
        // Test 4:3 aspect ratio
        context.setParameter("aspect_ratio", "1.333");
        context.setParameter("min_width", 800);
        context.setParameter("max_width", 800);
        String result2 = generator.generate(context);
        String[] parts2 = result2.split("x");
        int width2 = Integer.parseInt(parts2[0]);
        int height2 = Integer.parseInt(parts2[1]);
        float ratio2 = (float) width2 / height2;
        assertThat(ratio2).isCloseTo(1.333f, within(0.02f));
    }
    
    @Test
    public void testGetName() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        assertThat(generator.getName()).isEqualTo("image_dimensions");
    }
    
    @Test
    public void testGetSupportedParameters() {
        ImageDimensionsGenerator generator = new ImageDimensionsGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder(
            "min_width", "max_width", "min_height", "max_height", 
            "maintain_aspect_ratio", "aspect_ratio", "format");
    }
    
    private static org.assertj.core.data.Offset<Float> within(float value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}