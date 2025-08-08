package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScientificNotationGeneratorTest {
    
    @Test
    public void testGenerateScientificNotation() {
        ScientificNotationGenerator generator = new ScientificNotationGenerator(1.0, 9.99, -5, 5, 2);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).matches("\\d+\\.\\d+E[+-]\\d+");
            
            // Parse and verify components
            String[] parts = result.split("E");
            double mantissa = Double.parseDouble(parts[0]);
            int exponent = Integer.parseInt(parts[1]);
            
            assertThat(mantissa).isGreaterThanOrEqualTo(1.0);
            assertThat(mantissa).isLessThanOrEqualTo(9.99);
            assertThat(exponent).isGreaterThanOrEqualTo(-5);
            assertThat(exponent).isLessThanOrEqualTo(5);
        }
    }
    
    @Test
    public void testDefaultConstructor() {
        ScientificNotationGenerator generator = new ScientificNotationGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).matches("\\d+\\.\\d+E[+-]\\d+");
        }
    }
    
    @Test
    public void testDifferentPrecision() {
        ScientificNotationGenerator generator = new ScientificNotationGenerator(1.0, 9.99, -5, 5, 4);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            // Should have 4 decimal places
            String[] parts = result.split("E");
            String mantissaStr = parts[0];
            int decimalPlaces = mantissaStr.length() - mantissaStr.indexOf('.') - 1;
            assertThat(decimalPlaces).isEqualTo(4);
        }
    }
    
    @Test
    public void testInvalidParameters() {
        // Test negative minMantissa
        assertThatThrownBy(() -> new ScientificNotationGenerator(-1.0, 9.99, -5, 5, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Both minMantissa and maxMantissa must be positive");
        
        // Test negative maxMantissa
        assertThatThrownBy(() -> new ScientificNotationGenerator(1.0, -9.99, -5, 5, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Both minMantissa and maxMantissa must be positive");
        
        // Test minMantissa >= maxMantissa
        assertThatThrownBy(() -> new ScientificNotationGenerator(5.0, 3.0, -5, 5, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("minMantissa must be less than maxMantissa");
        
        // Test minExponent >= maxExponent
        assertThatThrownBy(() -> new ScientificNotationGenerator(1.0, 9.99, 5, 3, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("minExponent must be less than maxExponent");
        
        // Test negative precision
        assertThatThrownBy(() -> new ScientificNotationGenerator(1.0, 9.99, -5, 5, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Precision must be non-negative");
    }
    
    @Test
    public void testGetName() {
        ScientificNotationGenerator generator = new ScientificNotationGenerator();
        assertThat(generator.getName()).isEqualTo("scientific_notation");
    }
}