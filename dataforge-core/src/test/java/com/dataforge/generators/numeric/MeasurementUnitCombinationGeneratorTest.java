package com.dataforge.generators.numeric;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MeasurementUnitCombinationGeneratorTest {
    
    @Test
    public void testGenerateWithDefaultSettings() {
        MeasurementUnitCombinationGenerator generator = new MeasurementUnitCombinationGenerator();
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
            assertThat(result).contains(" ");
            
            // Split value and unit
            String[] parts = result.split(" ");
            assertThat(parts).hasSize(2);
            
            // Check that the first part is a valid number
            assertThat(parts[0]).matches("-?\\d+(\\.\\d+)?");
            
            // Check that we have a unit
            assertThat(parts[1]).isNotEmpty();
        }
    }
    
    @Test
    public void testGenerateWithSpecificType() {
        MeasurementUnitCombinationGenerator generator = new MeasurementUnitCombinationGenerator(
            0.0, 100.0, 2, "Length");
        GenerationContext context = new GenerationContext(1);
        
        // Get expected units for Length
        String[] expectedUnits = MeasurementUnitCombinationGenerator.getUnitsForType("Length");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            String[] parts = result.split(" ");
            assertThat(parts).hasSize(2);
            
            // Check that the unit is one of the expected length units
            assertThat(parts[1]).isIn((Object[]) expectedUnits);
        }
    }
    
    @Test
    public void testGenerateWithCustomUnits() {
        String[] customUnits = {"widgets", "gadgets", "thingamajigs"};
        MeasurementUnitCombinationGenerator generator = new MeasurementUnitCombinationGenerator(
            1.0, 10.0, 1, customUnits);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            String[] parts = result.split(" ");
            assertThat(parts).hasSize(2);
            
            // Check that the unit is one of the custom units
            assertThat(parts[1]).isIn((Object[]) customUnits);
        }
    }
    
    @Test
    public void testInvalidMeasurementType() {
        assertThatThrownBy(() -> new MeasurementUnitCombinationGenerator(
            0.0, 100.0, 2, "NonExistentType"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown measurement type: NonExistentType");
    }
    
    @Test
    public void testGetMeasurementTypes() {
        String[] types = MeasurementUnitCombinationGenerator.getMeasurementTypes();
        assertThat(types).isNotEmpty();
        assertThat(types).contains("Length", "Weight", "Volume", "Area", "Speed", 
                                 "Temperature", "Pressure", "Energy", "Power", "Frequency");
    }
    
    @Test
    public void testGetUnitsForType() {
        String[] lengthUnits = MeasurementUnitCombinationGenerator.getUnitsForType("Length");
        assertThat(lengthUnits).isNotNull();
        assertThat(lengthUnits).contains("mm", "cm", "m", "km", "in", "ft", "yd", "mi");
        
        String[] unknownUnits = MeasurementUnitCombinationGenerator.getUnitsForType("Unknown");
        assertThat(unknownUnits).isNull();
    }
    
    @Test
    public void testGetName() {
        MeasurementUnitCombinationGenerator generator = new MeasurementUnitCombinationGenerator();
        assertThat(generator.getName()).isEqualTo("measurement_unit");
    }
}