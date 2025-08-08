package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PercentageRateGeneratorTest {
    
    @Test
    public void testGeneratePercentageWithSign() {
        PercentageRateGenerator generator = new PercentageRateGenerator(0.0, 100.0, 2, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).endsWith("%");
            
            // Parse and verify the numeric part
            String numericPart = result.substring(0, result.length() - 1);
            double value = Double.parseDouble(numericPart);
            assertThat(value).isGreaterThanOrEqualTo(0.0);
            assertThat(value).isLessThanOrEqualTo(100.0);
            
            // Check for 2 decimal places
            int decimalIndex = numericPart.indexOf('.');
            if (decimalIndex != -1) {
                int decimalPlaces = numericPart.length() - decimalIndex - 1;
                assertThat(decimalPlaces).isLessThanOrEqualTo(2);
            }
        }
    }
    
    @Test
    public void testGenerateRateWithoutSign() {
        PercentageRateGenerator generator = new PercentageRateGenerator(0.0, 5.0, 4, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).doesNotEndWith("%");
            
            // Parse and verify the value
            double value = Double.parseDouble(result);
            assertThat(value).isGreaterThanOrEqualTo(0.0);
            assertThat(value).isLessThanOrEqualTo(5.0);
            
            // Check for 4 decimal places
            int decimalIndex = result.indexOf('.');
            if (decimalIndex != -1) {
                int decimalPlaces = result.length() - decimalIndex - 1;
                assertThat(decimalPlaces).isLessThanOrEqualTo(4);
            }
        }
    }
    
    @Test
    public void testDefaultConstructor() {
        PercentageRateGenerator generator = new PercentageRateGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).endsWith("%");
            
            // Parse and verify the numeric part
            String numericPart = result.substring(0, result.length() - 1);
            double value = Double.parseDouble(numericPart);
            assertThat(value).isGreaterThanOrEqualTo(0.0);
            assertThat(value).isLessThanOrEqualTo(100.0);
        }
    }
    
    @Test
    public void testNegativeValues() {
        PercentageRateGenerator generator = new PercentageRateGenerator(-5.0, 5.0, 2, true);
        GenerationContext context = new GenerationContext(1);
        
        boolean foundNegative = false;
        boolean foundPositive = false;
        
        for (int i = 0; i < 1000; i++) { // More iterations to increase chance of finding both
            String result = generator.generate(context);
            String numericPart = result.substring(0, result.length() - 1);
            double value = Double.parseDouble(numericPart);
            
            if (value < 0) {
                foundNegative = true;
            } else if (value > 0) {
                foundPositive = true;
            }
            
            assertThat(value).isGreaterThanOrEqualTo(-5.0);
            assertThat(value).isLessThanOrEqualTo(5.0);
            
            if (foundNegative && foundPositive) {
                break;
            }
        }
        
        assertThat(foundNegative).isTrue();
        assertThat(foundPositive).isTrue();
    }
    
    @Test
    public void testInvalidParameters() {
        // Test min >= max
        assertThatThrownBy(() -> new PercentageRateGenerator(100.0, 50.0, 2, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Min must be less than max");
        
        assertThatThrownBy(() -> new PercentageRateGenerator(50.0, 50.0, 2, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Min must be less than max");
        
        // Test negative precision
        assertThatThrownBy(() -> new PercentageRateGenerator(0.0, 100.0, -1, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Precision must be non-negative");
    }
    
    @Test
    public void testGetName() {
        PercentageRateGenerator generator = new PercentageRateGenerator();
        assertThat(generator.getName()).isEqualTo("percentage_rate");
    }
}