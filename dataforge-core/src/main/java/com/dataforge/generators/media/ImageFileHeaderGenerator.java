package com.dataforge.generators.media;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 图像文件头生成器
 * 支持生成常见图像格式的文件头字节序列
 */
public class ImageFileHeaderGenerator implements DataGenerator<byte[]> {

    private static final Map<String, byte[]> IMAGE_HEADERS = Map.of(
        "PNG", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A},
        "JPEG", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
        "GIF", new byte[]{'G', 'I', 'F', '8'},
        "BMP", new byte[]{'B', 'M'},
        "TIFF", new byte[]{'I', 'I', 0x2A, 0x00},
        "WEBP", new byte[]{'R', 'I', 'F', 'F'}
    );

    private final Random random;

    public ImageFileHeaderGenerator() {
        this.random = new Random();
    }

    @Override
    public byte[] generate(GenerationContext context) {
        String format = (String) context.getParameter("format", "PNG");
        boolean corrupt = (Boolean) context.getParameter("corrupt", false);
        
        byte[] header = IMAGE_HEADERS.getOrDefault(format.toUpperCase(), IMAGE_HEADERS.get("PNG"));
        
        if (corrupt) {
            header = header.clone();
            if (header.length > 0) {
                int corruptIndex = random.nextInt(header.length);
                header[corruptIndex] = (byte) (random.nextInt(256) - 128);
            }
        }
        
        return header;
    }

    @Override
    public String getName() {
        return "image_header";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("format", "corrupt");
    }

    public static List<String> getSupportedFormats() {
        return Collections.unmodifiableList(Arrays.asList("PNG", "JPEG", "GIF", "BMP", "TIFF", "WEBP"));
    }
}