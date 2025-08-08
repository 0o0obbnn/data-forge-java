package com.dataforge.generators.temporal;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Date generator for generating dates within a specified range.
 */
public class DateGenerator implements DataGenerator<String> {
    
    private final LocalDate minDate;
    private final LocalDate maxDate;
    private final String format;
    
    public DateGenerator() {
        this(LocalDate.now().minusYears(5), LocalDate.now(), "yyyy-MM-dd");
    }
    
    public DateGenerator(LocalDate minDate, LocalDate maxDate, String format) {
        if (minDate.isAfter(maxDate)) {
            throw new IllegalArgumentException("Min date cannot be after max date");
        }
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.format = format != null ? format : "yyyy-MM-dd";
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a random date between minDate and maxDate
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay = minDay + random.nextInt((int) (maxDay - minDay + 1));
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        
        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return randomDate.format(formatter);
    }
}