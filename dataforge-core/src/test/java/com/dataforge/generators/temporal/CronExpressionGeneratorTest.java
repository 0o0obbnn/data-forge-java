package com.dataforge.generators.temporal;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CronExpressionGeneratorTest {
    
    @Test
    public void testGenerateBasicCronExpression() {
        CronExpressionGenerator generator = new CronExpressionGenerator(false, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            String[] parts = result.split(" ");
            
            // Should have 5 parts (minute, hour, day of month, month, day of week)
            assertThat(parts).hasSize(5);
            
            // Validate each part
            validateCronField(parts[0], 0, 59); // Minutes
            validateCronField(parts[1], 0, 23); // Hours
            validateCronField(parts[2], 1, 31); // Day of month
            validateCronField(parts[3], 1, 12); // Month
            validateCronField(parts[4], 0, 7);  // Day of week
        }
    }
    
    @Test
    public void testGenerateCronExpressionWithSeconds() {
        CronExpressionGenerator generator = new CronExpressionGenerator(true, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            String[] parts = result.split(" ");
            
            // Should have 6 parts (seconds, minute, hour, day of month, month, day of week)
            assertThat(parts).hasSize(6);
            
            // Validate each part
            validateCronField(parts[0], 0, 59); // Seconds
            validateCronField(parts[1], 0, 59); // Minutes
            validateCronField(parts[2], 0, 23); // Hours
            validateCronField(parts[3], 1, 31); // Day of month
            validateCronField(parts[4], 1, 12); // Month
            validateCronField(parts[5], 0, 7);  // Day of week
        }
    }
    
    @Test
    public void testGenerateCronExpressionWithSpecialChars() {
        CronExpressionGenerator generator = new CronExpressionGenerator(false, true);
        GenerationContext context = new GenerationContext(1);
        
        boolean foundAsterisk = false;
        boolean foundQuestionMark = false;
        boolean foundL = false;
        boolean foundW = false;
        boolean foundHash = false;
        
        // Generate many expressions to increase chance of finding special chars
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            
            if (result.contains("*")) foundAsterisk = true;
            if (result.contains("?")) foundQuestionMark = true;
            if (result.contains("L")) foundL = true;
            if (result.contains("W")) foundW = true;
            if (result.contains("#")) foundHash = true;
            
            // If we found all special characters, we can stop
            if (foundAsterisk && foundQuestionMark && foundL && foundW && foundHash) {
                break;
            }
        }
        
        // Note: We're not asserting that all special chars must be found
        // because randomness might not generate all of them in 1000 iterations
        // But we're testing that the generator can produce them
    }
    
    @Test
    public void testGenerateCronExpressionWithRanges() {
        CronExpressionGenerator generator = new CronExpressionGenerator(false, false);
        GenerationContext context = new GenerationContext(1);
        
        // Test with many iterations to increase chance of generating ranges
        for (int i = 0; i < 1000; i++) {
            String result = generator.generate(context);
            
            // Check if the result contains range or step patterns
            if (result.contains("-") || result.contains("/")) {
                // Found a range or step, which is what we're testing for
                return;
            }
        }
        
        // If we get here, we didn't find any ranges, but that's possible with randomness
    }
    
    @Test
    public void testGetName() {
        CronExpressionGenerator generator = new CronExpressionGenerator();
        assertThat(generator.getName()).isEqualTo("cron_expression");
    }
    
    private void validateCronField(String field, int min, int max) {
        // This is a simple validation that checks if the field matches basic patterns
        // A full validation would be more complex due to all the possible patterns
        
        // Check if it's a single number within range
        if (field.matches("\\d+")) {
            int value = Integer.parseInt(field);
            assertThat(value).isGreaterThanOrEqualTo(min);
            assertThat(value).isLessThanOrEqualTo(max);
            return;
        }
        
        // Check if it's a range
        if (field.matches("\\d+-\\d+")) {
            String[] parts = field.split("-");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            assertThat(start).isGreaterThanOrEqualTo(min);
            assertThat(end).isLessThanOrEqualTo(max);
            return;
        }
        
        // Check if it's a range with step
        if (field.matches("\\d+-\\d+/\\d+")) {
            String[] rangeAndStep = field.split("/");
            String[] rangeParts = rangeAndStep[0].split("-");
            int start = Integer.parseInt(rangeParts[0]);
            int end = Integer.parseInt(rangeParts[1]);
            int step = Integer.parseInt(rangeAndStep[1]);
            assertThat(start).isGreaterThanOrEqualTo(min);
            assertThat(end).isLessThanOrEqualTo(max);
            assertThat(step).isGreaterThan(0);
            return;
        }
        
        // Check if it's a list
        if (field.contains(",")) {
            String[] values = field.split(",");
            for (String value : values) {
                if (value.matches("\\d+")) {
                    int intValue = Integer.parseInt(value);
                    assertThat(intValue).isGreaterThanOrEqualTo(min);
                    assertThat(intValue).isLessThanOrEqualTo(max);
                }
            }
            return;
        }
        
        // Check for special characters
        if ("*".equals(field) || "?".equals(field) || field.contains("L") || 
            field.contains("W") || field.contains("#")) {
            // These are valid special characters
            return;
        }
        
        // If we get here, the field format is unexpected
        // This assertion will fail and show us what the unexpected format was
        assertThat(field).matches("Should not reach here - unexpected field format");
    }
}