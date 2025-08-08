package com.dataforge.generators.media;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 图片尺寸生成器
 * 支持生成图片的宽度和高度像素值
 */
public class ImageDimensionsGenerator implements DataGenerator<String> {

    private final Random random;

    public ImageDimensionsGenerator() {
        this.random = new Random();
    }

    @Override
    public String generate(GenerationContext context) {
        // Get width parameters
        Object minWidthObj = context.getParameter("min_width", "100");
        int minWidth = Integer.parseInt(minWidthObj.toString());
        
        Object maxWidthObj = context.getParameter("max_width", "1920");
        int maxWidth = Integer.parseInt(maxWidthObj.toString());
        
        // Get height parameters
        Object minHeightObj = context.getParameter("min_height", "100");
        int minHeight = Integer.parseInt(minHeightObj.toString());
        
        Object maxHeightObj = context.getParameter("max_height", "1080");
        int maxHeight = Integer.parseInt(maxHeightObj.toString());
        
        // Get aspect ratio parameters
        Object maintainAspectRatioObj = context.getParameter("maintain_aspect_ratio", false);
        boolean maintainAspectRatio = Boolean.parseBoolean(maintainAspectRatioObj.toString());
        
        Object aspectRatioObj = context.getParameter("aspect_ratio", "1.777");
        float aspectRatio = Float.parseFloat(aspectRatioObj.toString());
        
        // Get format
        Object formatObj = context.getParameter("format", "{width}x{height}");
        String format = formatObj.toString();
        
        int width = minWidth + random.nextInt(Math.max(1, maxWidth - minWidth + 1));
        int height;
        
        if (maintainAspectRatio) {
            height = Math.round(width / aspectRatio);
            // Ensure height is within bounds
            height = Math.max(minHeight, Math.min(maxHeight, height));
        } else {
            height = minHeight + random.nextInt(Math.max(1, maxHeight - minHeight + 1));
        }
        
        return format.replace("{width}", String.valueOf(width))
                    .replace("{height}", String.valueOf(height));
    }

    @Override
    public String getName() {
        return "image_dimensions";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("min_width", "max_width", "min_height", "max_height", 
                           "maintain_aspect_ratio", "aspect_ratio", "format");
    }
}