package com.dataforge.generators.media;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ImageFileHeaderGeneratorTest {
    
    @Test
    public void testGeneratePngHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "PNG");
        
        byte[] result = generator.generate(context);
        
        // PNG header: 89 50 4E 47 0D 0A 1A 0A
        assertThat(result).containsExactly((byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
    }
    
    @Test
    public void testGenerateJpegHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "JPEG");
        
        byte[] result = generator.generate(context);
        
        // JPEG header: FF D8 FF
        assertThat(result).containsExactly((byte) 0xFF, (byte) 0xD8, (byte) 0xFF);
    }
    
    @Test
    public void testGenerateGifHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "GIF");
        
        byte[] result = generator.generate(context);
        
        // GIF header: 47 49 46 38 (GIF8)
        assertThat(result).containsExactly((byte) 'G', (byte) 'I', (byte) 'F', (byte) '8');
    }
    
    @Test
    public void testGenerateBmpHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "BMP");
        
        byte[] result = generator.generate(context);
        
        // BMP header: 42 4D (BM)
        assertThat(result).containsExactly((byte) 'B', (byte) 'M');
    }
    
    @Test
    public void testGenerateTiffHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "TIFF");
        
        byte[] result = generator.generate(context);
        
        // TIFF header: 49 49 2A 00 (II*)
        assertThat(result).containsExactly((byte) 'I', (byte) 'I', 0x2A, 0x00);
    }
    
    @Test
    public void testGenerateWebpHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "WEBP");
        
        byte[] result = generator.generate(context);
        
        // WEBP header: 52 49 46 46 (RIFF)
        assertThat(result).containsExactly((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F');
    }
    
    @Test
    public void testGenerateDefaultHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        
        byte[] result = generator.generate(context);
        
        // Default should be PNG
        assertThat(result).containsExactly((byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
    }
    
    @Test
    public void testGenerateWithInvalidFormat() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "INVALID");
        
        byte[] result = generator.generate(context);
        
        // Should fall back to PNG
        assertThat(result).containsExactly((byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
    }
    
    @Test
    public void testGenerateCorruptedHeader() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("format", "PNG");
        context.setParameter("corrupt", true);
        
        byte[] result = generator.generate(context);
        
        // Should be PNG header but with one byte corrupted
        assertThat(result).hasSize(8);
        
        // Count how many bytes differ from the original PNG header
        byte[] original = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        int diffCount = 0;
        for (int i = 0; i < result.length; i++) {
            if (result[i] != original[i]) {
                diffCount++;
            }
        }
        
        // Exactly one byte should be different
        assertThat(diffCount).isEqualTo(1);
    }
    
    @Test
    public void testGetSupportedFormats() {
        assertThat(ImageFileHeaderGenerator.getSupportedFormats())
            .containsExactlyInAnyOrder("PNG", "JPEG", "GIF", "BMP", "TIFF", "WEBP");
    }
    
    @Test
    public void testGetName() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        assertThat(generator.getName()).isEqualTo("image_header");
    }
    
    @Test
    public void testGetSupportedParameters() {
        ImageFileHeaderGenerator generator = new ImageFileHeaderGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder("format", "corrupt");
    }
}