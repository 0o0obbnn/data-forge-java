package com.dataforge.examples;

import com.dataforge.core.GenerationContext;

/**
 * Example demonstrating the use of DataGenerationCache.
 */
public class CacheExample {
    
    public static void main(String[] args) {
        // Create a cached name generator
        CachedNameGenerator cachedGenerator = new CachedNameGenerator();
        
        // Create a generation context
        GenerationContext context = new GenerationContext(10);
        
        System.out.println("Generating names with caching:");
        
        // Generate 10 names
        for (int i = 0; i < 10; i++) {
            String name = cachedGenerator.generate(context);
            System.out.println((i + 1) + ". " + name);
        }
        
        // Print cache statistics
        cachedGenerator.printCacheStats();
        
        System.out.println("\nGenerating more names to see cache eviction in action:");
        
        // Generate 10 more names to see cache eviction
        for (int i = 0; i < 10; i++) {
            String name = cachedGenerator.generate(context);
            System.out.println((i + 11) + ". " + name);
        }
        
        // Print cache statistics again
        cachedGenerator.printCacheStats();
    }
}