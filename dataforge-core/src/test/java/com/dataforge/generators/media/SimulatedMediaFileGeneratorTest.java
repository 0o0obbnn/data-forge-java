package com.dataforge.generators.media;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SimulatedMediaFileGeneratorTest {
    
    @Test
    public void testGenerateDefaultMediaFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            // Should be a hex string
            assertThat(result).matches("[0-9A-F]+");
            
            // Default size is 1024 bytes + header, so result should be quite long
            assertThat(result.length()).isGreaterThan(2000);
        }
    }
    
    @Test
    public void testGeneratePngFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "image_png");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with PNG header: 89 50 4E 47 0D 0A 1A 0A
        assertThat(result).startsWith("89504E470D0A1A0A");
    }
    
    @Test
    public void testGenerateCorruptedPngFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "image_png");
        context.setParameter("corrupted", true);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with corrupted PNG header: 89 50 4E 47 00
        assertThat(result).startsWith("89504E4700");
    }
    
    @Test
    public void testGenerateJpegFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "image_jpeg");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with JPEG header: FF D8 FF
        assertThat(result).startsWith("FFD8FF");
    }
    
    @Test
    public void testGenerateGifFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "image_gif");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with GIF header: 47 49 46 38 39 61 (GIF89a)
        assertThat(result).startsWith("474946383961");
    }
    
    @Test
    public void testGeneratePdfFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "pdf");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with PDF header: 25 50 44 46 2D 31 2E 34 0A (%PDF-1.4\n)
        assertThat(result).startsWith("255044462D312E340A");
    }
    
    @Test
    public void testGenerateZipFile() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "zip");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with ZIP header: 50 4B 03 04 (PK..)
        assertThat(result).startsWith("504B0304");
    }
    
    @Test
    public void testGenerateMp3File() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("type", "mp3");
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should start with MP3 header: 49 44 33 (ID3)
        assertThat(result).startsWith("494433");
    }
    
    @Test
    public void testGenerateCustomSize() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("size", 512);
        
        String result = generator.generate(context);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Should be shorter than default (1024 bytes)
        assertThat(result.length()).isLessThan(3000); // 1024 bytes * 2 chars/byte + header
    }
    
    @Test
    public void testGetName() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        assertThat(generator.getName()).isEqualTo("simulated_media_file");
    }
    
    @Test
    public void testGetSupportedParameters() {
        SimulatedMediaFileGenerator generator = new SimulatedMediaFileGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder("type", "corrupted", "size");
    }
}