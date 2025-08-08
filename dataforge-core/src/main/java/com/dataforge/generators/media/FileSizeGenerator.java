package com.dataforge.generators.media;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 文件大小生成器
 * 支持生成指定范围内的文件大小，带单位显示
 */
public class FileSizeGenerator implements DataGenerator<String> {

    private static final List<String> UNITS = Arrays.asList("B", "KB", "MB", "GB", "TB");
    private final Random random;

    public FileSizeGenerator() {
        this.random = new Random();
    }

    @Override
    public String generate(GenerationContext context) {
        // Get min value
        Object minObj = context.getParameter("min", "1");
        long min = Long.parseLong(minObj.toString());
        
        // Get max value
        Object maxObj = context.getParameter("max", "100");
        long max = Long.parseLong(maxObj.toString());
        
        // Get unit
        Object unitObj = context.getParameter("unit", "KB");
        String unit = unitObj.toString();
        
        // Get include_unit flag
        Object includeUnitObj = context.getParameter("include_unit", true);
        boolean includeUnit = Boolean.parseBoolean(includeUnitObj.toString());
        
        long minBytes = convertToBytes(min, unit);
        long maxBytes = convertToBytes(max, unit);
        
        long sizeInBytes = minBytes + random.nextLong(Math.max(1, maxBytes - minBytes + 1));
        
        if (includeUnit) {
            return formatFileSize(sizeInBytes);
        } else {
            return String.valueOf(sizeInBytes);
        }
    }

    private long convertToBytes(long value, String unit) {
        switch (unit.toUpperCase()) {
            case "B":
                return value;
            case "KB":
                return value * 1024;
            case "MB":
                return value * 1024 * 1024;
            case "GB":
                return value * 1024L * 1024 * 1024;
            case "TB":
                return value * 1024L * 1024 * 1024 * 1024;
            default:
                return value * 1024; // Default to KB
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes == 0) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    @Override
    public String getName() {
        return "file_size";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("min", "max", "unit", "include_unit");
    }
}