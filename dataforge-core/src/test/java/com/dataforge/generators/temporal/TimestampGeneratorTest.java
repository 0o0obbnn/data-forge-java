package com.dataforge.generators.temporal;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class TimestampGeneratorTest {
    
    @Test
    public void testDefaultTimestampGeneration() {
        TimestampGenerator generator = new TimestampGenerator();
        GenerationContext context = new GenerationContext(1);
        
        String timestampStr = generator.generate(context);
        assertNotNull(timestampStr);
        assertFalse(timestampStr.isEmpty());
        
        // Parse the timestamp to ensure it's valid
        long timestamp = Long.parseLong(timestampStr);
        assertTrue(timestamp > 0);
    }
    
    @Test
    public void testMillisecondsTimestampGeneration() {
        LocalDateTime minDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime maxDateTime = LocalDateTime.of(2020, 12, 31, 23, 59);
        TimestampGenerator generator = new TimestampGenerator(minDateTime, maxDateTime, true, null);
        GenerationContext context = new GenerationContext(1);
        
        String timestampStr = generator.generate(context);
        assertNotNull(timestampStr);
        assertFalse(timestampStr.isEmpty());
        
        // Parse the timestamp to ensure it's valid
        long timestamp = Long.parseLong(timestampStr);
        assertTrue(timestamp > 0);
    }
    
    @Test
    public void testFormattedTimestampGeneration() {
        LocalDateTime minDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime maxDateTime = LocalDateTime.of(2020, 12, 31, 23, 59);
        TimestampGenerator generator = new TimestampGenerator(minDateTime, maxDateTime, false, "yyyy-MM-dd HH:mm:ss");
        GenerationContext context = new GenerationContext(1);
        
        String timestampStr = generator.generate(context);
        assertNotNull(timestampStr);
        assertFalse(timestampStr.isEmpty());
        assertTrue(timestampStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
    
    @Test
    public void testInvalidDateTimeRange() {
        LocalDateTime minDateTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime maxDateTime = LocalDateTime.of(2020, 12, 31, 23, 59);
        
        org.testng.Assert.assertThrows(IllegalArgumentException.class, () -> {
            new TimestampGenerator(minDateTime, maxDateTime, false, null);
        });
    }
}