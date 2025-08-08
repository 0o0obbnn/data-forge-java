package com.dataforge.generators.temporal;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Time generator for generating times within a specified range.
 */
public class TimeGenerator implements DataGenerator<String> {
    
    private final LocalTime minTime;
    private final LocalTime maxTime;
    private final String format;
    
    public TimeGenerator() {
        this(LocalTime.MIDNIGHT, LocalTime.MAX, "HH:mm:ss");
    }
    
    public TimeGenerator(LocalTime minTime, LocalTime maxTime, String format) {
        if (minTime.isAfter(maxTime)) {
            throw new IllegalArgumentException("Min time cannot be after max time");
        }
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.format = format != null ? format : "HH:mm:ss";
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a random time between minTime and maxTime
        int minSeconds = minTime.toSecondOfDay();
        int maxSeconds = maxTime.toSecondOfDay();
        int randomSeconds = minSeconds + random.nextInt(maxSeconds - minSeconds + 1);
        LocalTime randomTime = LocalTime.ofSecondOfDay(randomSeconds);
        
        // Format the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return randomTime.format(formatter);
    }
}