package com.dataforge.generators.temporal;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.testng.Assert.*;

public class DateGeneratorTest {
    
    @Test
    public void testDefaultDateGeneration() {
        DateGenerator generator = new DateGenerator();
        GenerationContext context = new GenerationContext(1);
        
        String dateStr = generator.generate(context);
        assertNotNull(dateStr);
        assertFalse(dateStr.isEmpty());
        
        // Parse the date to ensure it's valid
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(date.isAfter(LocalDate.now().minusYears(5)) || date.isEqual(LocalDate.now().minusYears(5)));
        assertTrue(date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()));
    }
    
    @Test
    public void testCustomDateGeneration() {
        LocalDate minDate = LocalDate.of(2020, 1, 1);
        LocalDate maxDate = LocalDate.of(2020, 12, 31);
        DateGenerator generator = new DateGenerator(minDate, maxDate, "dd/MM/yyyy");
        GenerationContext context = new GenerationContext(1);
        
        String dateStr = generator.generate(context);
        assertNotNull(dateStr);
        assertFalse(dateStr.isEmpty());
        
        // Parse the date to ensure it's valid
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertTrue(date.isAfter(minDate) || date.isEqual(minDate));
        assertTrue(date.isBefore(maxDate) || date.isEqual(maxDate));
    }
    
    @Test
    public void testInvalidDateRange() {
        LocalDate minDate = LocalDate.of(2021, 1, 1);
        LocalDate maxDate = LocalDate.of(2020, 12, 31);
        
        org.testng.Assert.assertThrows(IllegalArgumentException.class, () -> {
            new DateGenerator(minDate, maxDate, "yyyy-MM-dd");
        });
    }
}