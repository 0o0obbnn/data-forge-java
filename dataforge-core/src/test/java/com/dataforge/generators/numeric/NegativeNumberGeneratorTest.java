package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NegativeNumberGeneratorTest {
    
    @Test
    public void testGenerateNegativeInteger() {
        NegativeNumberGenerator generator = new NegativeNumberGenerator(-100, -1, true);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            Number result = generator.generate(context);
            assertThat(result).isInstanceOf(Integer.class);
            int value = result.intValue();
            assertThat(value).isLessThan(0);
            assertThat(value).isGreaterThanOrEqualTo(-100);
            assertThat(value).isLessThanOrEqualTo(-1);
        }
    }
    
    @Test
    public void testGenerateNegativeDouble() {
        NegativeNumberGenerator generator = new NegativeNumberGenerator(-100.5, -1.5, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            Number result = generator.generate(context);
            assertThat(result).isInstanceOf(Double.class);
            double value = result.doubleValue();
            assertThat(value).isLessThan(0);
            assertThat(value).isGreaterThanOrEqualTo(-100.5);
            assertThat(value).isLessThanOrEqualTo(-1.5);
        }
    }
    
    @Test
    public void testDefaultConstructor() {
        NegativeNumberGenerator generator = new NegativeNumberGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            Number result = generator.generate(context);
            double value = result.doubleValue();
            assertThat(value).isLessThan(0);
            assertThat(value).isGreaterThanOrEqualTo(-1000);
            assertThat(value).isLessThanOrEqualTo(-1);
        }
    }
    
    @Test
    public void testInvalidParameters() {
        // Test positive min
        assertThatThrownBy(() -> new NegativeNumberGenerator(1, -1, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Both min and max must be negative");
        
        // Test positive max
        assertThatThrownBy(() -> new NegativeNumberGenerator(-10, 5, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Both min and max must be negative");
        
        // Test min > max
        assertThatThrownBy(() -> new NegativeNumberGenerator(-1, -10, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Min cannot be greater than max");
    }
    
    @Test
    public void testGetName() {
        NegativeNumberGenerator generator = new NegativeNumberGenerator();
        assertThat(generator.getName()).isEqualTo("negative_number");
    }
}