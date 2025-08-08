package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Duplicate data generator for generating duplicate records or values with configurable duplication rates.
 * Useful for testing deduplication algorithms and handling duplicate data scenarios.
 */
public class DuplicateDataGenerator implements DataGenerator<Object> {
    
    private final Random random;
    
    /**
     * Creates a duplicate data generator with default settings.
     */
    public DuplicateDataGenerator() {
        this.random = new Random();
    }
    
    @Override
    public Object generate(GenerationContext context) {
        // Get parameters
        Object duplicationRateObj = context.getParameter("duplication_rate", "0.1");
        double duplicationRate = Double.parseDouble(duplicationRateObj.toString());
        
        Object generatorTypeObj = context.getParameter("generator_type", "name");
        String generatorType = generatorTypeObj.toString();
        
        Object duplicateCountObj = context.getParameter("duplicate_count", "2");
        int duplicateCount = Integer.parseInt(duplicateCountObj.toString());
        
        // Ensure duplication rate is between 0 and 1
        duplicationRate = Math.max(0.0, Math.min(1.0, duplicationRate));
        
        // Decide whether to generate a duplicate or a new value
        if (random.nextDouble() < duplicationRate) {
            // Generate a duplicate
            return generateDuplicateValue(context, generatorType, duplicateCount);
        } else {
            // Generate a new unique value
            return generateNewValue(context, generatorType);
        }
    }
    
    /**
     * Generates a duplicate value by reusing previously generated values.
     * 
     * @param context the generation context
     * @param generatorType the type of generator to use for base value
     * @param duplicateCount the number of duplicates to generate
     * @return the duplicate value
     */
    private Object generateDuplicateValue(GenerationContext context, String generatorType, int duplicateCount) {
        // Get the duplicate values from context or create new ones
        @SuppressWarnings("unchecked")
        Map<String, Object> duplicateValues = (Map<String, Object>) context.getParameter("duplicate_values", new HashMap<String, Object>());
        
        // Check if we already have duplicates for this generator type
        String key = generatorType + "_duplicates";
        if (duplicateValues.containsKey(key)) {
            // Return an existing duplicate
            return duplicateValues.get(key);
        } else {
            // Generate a new base value and store duplicates
            Object baseValue = generateNewValue(context, generatorType);
            duplicateValues.put(key, baseValue);
            context.setParameter("duplicate_values", duplicateValues);
            return baseValue;
        }
    }
    
    /**
     * Generates a new unique value using the specified generator.
     * 
     * @param context the generation context
     * @param generatorType the type of generator to use
     * @return the new unique value
     */
    private Object generateNewValue(GenerationContext context, String generatorType) {
        try {
            DataGenerator<?> generator = GeneratorFactory.createGenerator(generatorType);
            return generator.generate(context);
        } catch (Exception e) {
            // Fallback to string generator if the specified type is not available
            DataGenerator<?> stringGenerator = GeneratorFactory.createGenerator("string");
            return stringGenerator.generate(context);
        }
    }
    
    @Override
    public String getName() {
        return "duplicate_data";
    }
    
    @Override
    public java.util.List<String> getSupportedParameters() {
        return java.util.Arrays.asList("duplication_rate", "generator_type", "duplicate_count");
    }
}