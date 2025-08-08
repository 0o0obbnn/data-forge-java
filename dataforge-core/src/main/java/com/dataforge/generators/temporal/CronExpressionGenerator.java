package com.dataforge.generators.temporal;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Cron expression generator for generating cron expressions with various patterns.
 * Supports standard cron format with 5 or 6 fields (with optional seconds).
 */
public class CronExpressionGenerator implements DataGenerator<String> {
    
    private final boolean includeSeconds;
    private final boolean allowSpecialChars;
    
    /**
     * Creates a cron expression generator with default settings (no seconds, allows special chars).
     */
    public CronExpressionGenerator() {
        this(false, true);
    }
    
    /**
     * Creates a cron expression generator.
     * 
     * @param includeSeconds whether to include seconds field (6 fields instead of 5)
     * @param allowSpecialChars whether to allow special characters (*, ?, -, /, L, W, #)
     */
    public CronExpressionGenerator(boolean includeSeconds, boolean allowSpecialChars) {
        this.includeSeconds = includeSeconds;
        this.allowSpecialChars = allowSpecialChars;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        StringBuilder cron = new StringBuilder();
        
        // Generate seconds field if requested
        if (includeSeconds) {
            cron.append(generateField(random, 0, 59, "seconds")).append(" ");
        }
        
        // Generate minutes field (0-59)
        cron.append(generateField(random, 0, 59, "minutes")).append(" ");
        
        // Generate hours field (0-23)
        cron.append(generateField(random, 0, 23, "hours")).append(" ");
        
        // Generate day of month field (1-31)
        cron.append(generateField(random, 1, 31, "dayOfMonth")).append(" ");
        
        // Generate month field (1-12)
        cron.append(generateField(random, 1, 12, "month")).append(" ");
        
        // Generate day of week field (0-7, where both 0 and 7 represent Sunday)
        cron.append(generateField(random, 0, 7, "dayOfWeek"));
        
        return cron.toString();
    }
    
    private String generateField(Random random, int min, int max, String fieldType) {
        // With some probability, use special characters or patterns
        if (allowSpecialChars && random.nextDouble() < 0.3) {
            return generateSpecialPattern(random, min, max, fieldType);
        }
        
        // With some probability, use a range
        if (random.nextDouble() < 0.2) {
            int start = min + random.nextInt((max - min) / 2);
            int end = start + 1 + random.nextInt(max - start);
            if (random.nextDouble() < 0.5) {
                // Range with step
                int step = 1 + random.nextInt(5);
                return start + "-" + end + "/" + step;
            } else {
                // Simple range
                return start + "-" + end;
            }
        }
        
        // With some probability, use a list
        if (random.nextDouble() < 0.2) {
            int count = 2 + random.nextInt(4); // 2-5 values
            StringBuilder list = new StringBuilder();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    list.append(",");
                }
                int value = min + random.nextInt(max - min + 1);
                list.append(value);
            }
            return list.toString();
        }
        
        // Otherwise, generate a single value or *
        if (random.nextDouble() < 0.1) {
            return "*";
        } else {
            return String.valueOf(min + random.nextInt(max - min + 1));
        }
    }
    
    private String generateSpecialPattern(Random random, int min, int max, String fieldType) {
        switch (random.nextInt(5)) {
            case 0:
                return "*"; // Every
            case 1:
                return "?"; // No specific value (used for day of month or day of week)
            case 2:
                // Last (L) - valid for day of month and day of week
                if ("dayOfMonth".equals(fieldType) || "dayOfWeek".equals(fieldType)) {
                    if (random.nextBoolean()) {
                        return "L";
                    } else {
                        // Last day of week (e.g., 6L for last Friday)
                        int day = 1 + random.nextInt(7);
                        return day + "L";
                    }
                }
                break;
            case 3:
                // Weekday (W) - valid only for day of month
                if ("dayOfMonth".equals(fieldType)) {
                    int day = min + random.nextInt(max - min + 1);
                    return day + "W";
                }
                break;
            case 4:
                // Nth day of week (e.g., 6#3 for third Friday)
                if ("dayOfWeek".equals(fieldType)) {
                    int day = 1 + random.nextInt(7);
                    int occurrence = 1 + random.nextInt(5);
                    return day + "#" + occurrence;
                }
                break;
        }
        
        // Fallback to regular value if special pattern not applicable
        return String.valueOf(min + random.nextInt(max - min + 1));
    }
    
    @Override
    public String getName() {
        return "cron_expression";
    }
}