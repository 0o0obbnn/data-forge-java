package com.dataforge.generators.temporal;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.testng.Assert.*;

public class TimeGeneratorTest {
    
    @Test
    public void testDefaultTimeGeneration() {
        TimeGenerator generator = new TimeGenerator();
        GenerationContext context = new GenerationContext(1);
        
        String timeStr = generator.generate(context);
        assertNotNull(timeStr);
        assertFalse(timeStr.isEmpty());
        
        // Parse the time to ensure it's valid
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
        assertNotNull(time);
    }
    
    @Test
    public void testCustomTimeGeneration() {
        LocalTime minTime = LocalTime.of(9, 0);
        LocalTime maxTime = LocalTime.of(17, 0);
        TimeGenerator generator = new TimeGenerator(minTime, maxTime, "HH:mm");
        GenerationContext context = new GenerationContext(1);
        
        String timeStr = generator.generate(context);
        assertNotNull(timeStr);
        assertFalse(timeStr.isEmpty());
        
        // Parse the time to ensure it's valid
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        assertTrue(time.isAfter(minTime) || time.equals(minTime));
        assertTrue(time.isBefore(maxTime) || time.equals(maxTime));
    }
    
    @Test
    public void testInvalidTimeRange() {
        LocalTime minTime = LocalTime.of(17, 0);
        LocalTime maxTime = LocalTime.of(9, 0);
        
        org.testng.Assert.assertThrows(IllegalArgumentException.class, () -> {
            new TimeGenerator(minTime, maxTime, "HH:mm:ss");
        });
    }
}