package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.generators.numeric.DecimalGenerator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Measurement unit combination generator for generating values with units (e.g., "1.5 kg", "100 m").
 */
public class MeasurementUnitCombinationGenerator implements DataGenerator<String> {
    
    // Predefined measurement types and their units
    private static final String[][] MEASUREMENT_UNITS = {
        {"Length", "mm", "cm", "m", "km", "in", "ft", "yd", "mi"},
        {"Weight", "mg", "g", "kg", "t", "oz", "lb"},
        {"Volume", "ml", "cl", "dl", "l", "hl", "cm³", "m³", "in³", "ft³", "gal", "qt", "pt"},
        {"Area", "mm²", "cm²", "m²", "km²", "ha", "ac", "in²", "ft²", "yd²", "mi²"},
        {"Speed", "m/s", "km/h", "mph", "kn"},
        {"Temperature", "°C", "°F", "K"},
        {"Pressure", "Pa", "kPa", "MPa", "bar", "mbar", "atm", "psi"},
        {"Energy", "J", "kJ", "MJ", "cal", "kcal", "Wh", "kWh"},
        {"Power", "W", "kW", "MW", "hp"},
        {"Frequency", "Hz", "kHz", "MHz", "GHz"}
    };
    
    private final DecimalGenerator valueGenerator;
    private final String[] units;
    private final String measurementType;
    
    /**
     * Creates a measurement unit combination generator with default settings.
     */
    public MeasurementUnitCombinationGenerator() {
        this(-1000.0, 1000.0, 2, "any");
    }
    
    /**
     * Creates a measurement unit combination generator.
     * 
     * @param min minimum value
     * @param max maximum value
     * @param precision number of decimal places
     * @param measurementType type of measurement (e.g., "Length", "Weight") or "any" for random selection
     */
    public MeasurementUnitCombinationGenerator(double min, double max, int precision, String measurementType) {
        this.valueGenerator = new DecimalGenerator(
            BigDecimal.valueOf(min), BigDecimal.valueOf(max), precision);
        this.measurementType = measurementType.toLowerCase();
        
        // Find units for the specified measurement type
        if ("any".equals(this.measurementType)) {
            // Collect all units from all measurement types
            StringBuilder allUnits = new StringBuilder();
            for (String[] typeAndUnits : MEASUREMENT_UNITS) {
                for (int i = 1; i < typeAndUnits.length; i++) {
                    if (allUnits.length() > 0) {
                        allUnits.append(",");
                    }
                    allUnits.append(typeAndUnits[i]);
                }
            }
            this.units = allUnits.toString().split(",");
        } else {
            // Find the specific measurement type
            this.units = findUnitsForType(this.measurementType);
            if (this.units == null) {
                throw new IllegalArgumentException("Unknown measurement type: " + measurementType);
            }
        }
    }
    
    /**
     * Creates a measurement unit combination generator with custom units.
     * 
     * @param min minimum value
     * @param max maximum value
     * @param precision number of decimal places
     * @param units array of custom units
     */
    public MeasurementUnitCombinationGenerator(double min, double max, int precision, String[] units) {
        this.valueGenerator = new DecimalGenerator(
            BigDecimal.valueOf(min), BigDecimal.valueOf(max), precision);
        this.units = Arrays.copyOf(units, units.length);
        this.measurementType = "custom";
    }
    
    private String[] findUnitsForType(String type) {
        for (String[] typeAndUnits : MEASUREMENT_UNITS) {
            if (typeAndUnits[0].equalsIgnoreCase(type)) {
                // Return all units except the first element (measurement type name)
                return Arrays.copyOfRange(typeAndUnits, 1, typeAndUnits.length);
            }
        }
        return null;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a value
        BigDecimal value = valueGenerator.generate(context);
        
        // Select a random unit
        String unit = units[random.nextInt(units.length)];
        
        // Combine value and unit
        return value.stripTrailingZeros().toPlainString() + " " + unit;
    }
    
    @Override
    public String getName() {
        return "measurement_unit";
    }
    
    /**
     * Gets the available measurement types.
     * 
     * @return array of measurement types
     */
    public static String[] getMeasurementTypes() {
        String[] types = new String[MEASUREMENT_UNITS.length];
        for (int i = 0; i < MEASUREMENT_UNITS.length; i++) {
            types[i] = MEASUREMENT_UNITS[i][0];
        }
        return types;
    }
    
    /**
     * Gets the units for a specific measurement type.
     * 
     * @param type measurement type
     * @return array of units for the type, or null if type not found
     */
    public static String[] getUnitsForType(String type) {
        for (String[] typeAndUnits : MEASUREMENT_UNITS) {
            if (typeAndUnits[0].equalsIgnoreCase(type)) {
                // Return all units except the first element (measurement type name)
                return Arrays.copyOfRange(typeAndUnits, 1, typeAndUnits.length);
            }
        }
        return null;
    }
}