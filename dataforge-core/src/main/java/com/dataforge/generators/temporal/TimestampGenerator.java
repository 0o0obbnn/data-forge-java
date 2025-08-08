package com.dataforge.generators.temporal;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Timestamp generator for generating timestamps.
 */
public class TimestampGenerator implements DataGenerator<String> {
    
    private final LocalDateTime minDateTime;
    private final LocalDateTime maxDateTime;
    private final boolean milliseconds;
    private final String format;
    
    public TimestampGenerator() {
        this(LocalDateTime.now().minusYears(1), LocalDateTime.now(), false, null);
    }
    
    public TimestampGenerator(LocalDateTime minDateTime, LocalDateTime maxDateTime, boolean milliseconds, String format) {
        if (minDateTime.isAfter(maxDateTime)) {
            throw new IllegalArgumentException("Min datetime cannot be after max datetime");
        }
        this.minDateTime = minDateTime;
        this.maxDateTime = maxDateTime;
        this.milliseconds = milliseconds;
        this.format = format;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a random datetime between minDateTime and maxDateTime
        long minSecond = minDateTime.toEpochSecond(java.time.ZoneOffset.UTC);
        long maxSecond = maxDateTime.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomSecond = minSecond + random.nextInt((int) (maxSecond - minSecond + 1));
        
        LocalDateTime randomDateTime;
        if (milliseconds) {
            int randomNano = random.nextInt(1000) * 1000000; // Convert milliseconds to nanoseconds
            randomDateTime = LocalDateTime.ofEpochSecond(randomSecond, randomNano, java.time.ZoneOffset.UTC);
        } else {
            randomDateTime = LocalDateTime.ofEpochSecond(randomSecond, 0, java.time.ZoneOffset.UTC);
        }
        
        // Format the timestamp
        if (format != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return randomDateTime.format(formatter);
        } else if (milliseconds) {
            return String.valueOf(randomDateTime.atZone(java.time.ZoneOffset.UTC).toInstant().toEpochMilli());
        } else {
            return String.valueOf(randomSecond);
        }
    }
}