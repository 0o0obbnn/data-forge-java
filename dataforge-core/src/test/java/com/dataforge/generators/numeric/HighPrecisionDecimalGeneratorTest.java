package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.math.BigDecimal;

public class HighPrecisionDecimalGeneratorTest {
    
    @Test
    public void testGenerateHighPrecisionDecimal() {
        HighPrecisionDecimalGenerator generator = new HighPrecisionDecimalGenerator(
            new BigDecimal("-100"), new BigDecimal("100"), 5);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            BigDecimal result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isGreaterThanOrEqualTo(new BigDecimal("-100"));
            assertThat(result).isLessThanOrEqualTo(new BigDecimal("100"));
            assertThat(result.scale()).isEqualTo(5); // Should have 5 decimal places
        }
    }
    
    @Test
    public void testDefaultConstructor() {
        HighPrecisionDecimalGenerator generator = new HighPrecisionDecimalGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            BigDecimal result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result.scale()).isEqualTo(10); // Default scale is 10
        }
    }
    
    @Test
    public void testDifferentScales() {
        // Test with scale of 0 (integer values)
        HighPrecisionDecimalGenerator generator0 = new HighPrecisionDecimalGenerator(
            new BigDecimal("-100"), new BigDecimal("100"), 0);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            BigDecimal result = generator0.generate(context);
            assertThat(result.scale()).isEqualTo(0);
        }
        
        // Test with scale of 20
        HighPrecisionDecimalGenerator generator20 = new HighPrecisionDecimalGenerator(
            new BigDecimal("-100"), new BigDecimal("100"), 20);
        
        for (int i = 0; i < 100; i++) {
            BigDecimal result = generator20.generate(context);
            assertThat(result.scale()).isEqualTo(20);
        }
    }
    
    @Test
    public void testInvalidParameters() {
        // Test min >= max
        assertThatThrownBy(() -> new HighPrecisionDecimalGenerator(
            new BigDecimal("100"), new BigDecimal("-100"), 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Min must be less than max");
        
        assertThatThrownBy(() -> new HighPrecisionDecimalGenerator(
            new BigDecimal("100"), new BigDecimal("100"), 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Min must be less than max");
        
        // Test negative scale
        assertThatThrownBy(() -> new HighPrecisionDecimalGenerator(
            new BigDecimal("-100"), new BigDecimal("100"), -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Scale must be non-negative");
    }
    
    @Test
    public void testGetName() {
        HighPrecisionDecimalGenerator generator = new HighPrecisionDecimalGenerator();
        assertThat(generator.getName()).isEqualTo("high_precision_decimal");
    }
}