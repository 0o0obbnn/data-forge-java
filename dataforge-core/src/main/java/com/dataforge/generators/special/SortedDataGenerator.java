package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;

import java.util.*;

/**
 * Sorted data generator for generating data in ascending, descending, or custom order.
 * Useful for testing sorting algorithms and ordered data processing.
 */
public class SortedDataGenerator implements DataGenerator<List<Object>> {
    
    private final Random random;
    
    /**
     * Creates a sorted data generator with default settings.
     */
    public SortedDataGenerator() {
        this.random = new Random();
    }
    
    @Override
    public List<Object> generate(GenerationContext context) {
        // Get parameters
        Object countObj = context.getParameter("count", "10");
        int count = Integer.parseInt(countObj.toString());
        
        Object sortOrderObj = context.getParameter("sort_order", "asc");
        String sortOrder = sortOrderObj.toString().toLowerCase();
        
        Object dataTypeObj = context.getParameter("data_type", "integer");
        String dataType = dataTypeObj.toString().toLowerCase();
        
        Object uniqueObj = context.getParameter("unique", true);
        boolean unique = Boolean.parseBoolean(uniqueObj.toString());
        
        // Generate raw data
        List<Object> rawData = new ArrayList<>();
        Set<Object> uniqueSet = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            Object value;
            do {
                value = generateValue(context, dataType);
            } while (unique && !uniqueSet.add(value));
            
            rawData.add(value);
        }
        
        // Sort the data
        sortData(rawData, sortOrder, dataType);
        
        return rawData;
    }
    
    /**
     * Generates a single value of the specified data type.
     * 
     * @param context the generation context
     * @param dataType the type of data to generate
     * @return the generated value
     */
    private Object generateValue(GenerationContext context, String dataType) {
        try {
            // Special handling for string type to ensure we get strings
            if ("string".equals(dataType)) {
                @SuppressWarnings("unchecked")
                DataGenerator<String> stringGenerator = (DataGenerator<String>) GeneratorFactory.createGenerator("name");
                return stringGenerator.generate(context);
            }
            
            DataGenerator<?> generator = GeneratorFactory.createGenerator(dataType);
            return generator.generate(context);
        } catch (Exception e) {
            // Fallback to integer generator if the specified type is not available
            @SuppressWarnings("unchecked")
            DataGenerator<Integer> integerGenerator = (DataGenerator<Integer>) GeneratorFactory.createGenerator("integer");
            return integerGenerator.generate(context);
        }
    }
    
    /**
     * Sorts the data according to the specified order and data type.
     * 
     * @param data the data to sort
     * @param sortOrder the sort order (asc, desc, custom)
     * @param dataType the data type
     */
    @SuppressWarnings({"unchecked", "unused"})
    private void sortData(List<Object> data, String sortOrder, String dataType) {
        switch (sortOrder) {
            case "desc":
                // Sort in descending order
                data.sort(Collections.reverseOrder());
                break;
            case "custom":
                // Custom sorting - shuffle the data
                Collections.shuffle(data, random);
                break;
            case "asc":
            default:
                // Sort in ascending order (default)
                try {
                    Collections.sort((List<Comparable<Object>>) (List<?>) data);
                } catch (ClassCastException e) {
                    // If elements are not comparable, leave them in original order
                }
                break;
        }
    }
    
    @Override
    public String getName() {
        return "sorted_data";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("count", "sort_order", "data_type", "unique");
    }
}