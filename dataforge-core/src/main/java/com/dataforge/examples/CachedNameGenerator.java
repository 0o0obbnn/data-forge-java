package com.dataforge.examples;

import com.dataforge.core.DataGenerationCache;
import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Example of a cached data generator that uses DataGenerationCache to avoid
 * regenerating the same values repeatedly.
 */
public class CachedNameGenerator implements DataGenerator<String> {
    
    // Sample names for demonstration
    private static final String[] NAMES = {
        "John Smith", "Jane Doe", "Michael Johnson", "Emily Brown", "David Wilson",
        "Sarah Davis", "Robert Miller", "Lisa Taylor", "James Anderson", "Patricia Thomas"
    };
    
    // Cache to store previously generated names
    private final DataGenerationCache<Integer, String> cache = new DataGenerationCache<>(5);
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        int index = random.nextInt(NAMES.length);
        
        // Check if we have this name in cache
        if (cache.containsKey(index)) {
            // Return cached value
            return cache.get(index);
        } else {
            // Generate new value and cache it
            String name = NAMES[index];
            cache.put(index, name);
            return name;
        }
    }
    
    // Method to demonstrate cache usage statistics
    public void printCacheStats() {
        System.out.println("Cache size: " + cache.size());
        System.out.println("Cache capacity: " + cache.getCapacity());
    }
}