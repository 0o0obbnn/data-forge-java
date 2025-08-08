package com.dataforge.examples;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.generators.basic.NameGenerator;

/**
 * Simple performance test to compare cached vs non-cached generation.
 */
public class PerformanceTest {
    
    public static void main(String[] args) {
        int iterations = 100000;
        
        // Test without caching
        DataGenerator<String> nameGenerator = new NameGenerator();
        GenerationContext context = new GenerationContext(iterations);
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            nameGenerator.generate(new GenerationContext(1));
        }
        long endTime = System.currentTimeMillis();
        
        System.out.println("Non-cached generation time for " + iterations + " iterations: " + 
                          (endTime - startTime) + " ms");
        
        // Test with caching
        CachedNameGenerator cachedNameGenerator = new CachedNameGenerator();
        
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            cachedNameGenerator.generate(new GenerationContext(1));
        }
        endTime = System.currentTimeMillis();
        
        System.out.println("Cached generation time for " + iterations + " iterations: " + 
                          (endTime - startTime) + " ms");
        
        // Show cache stats
        cachedNameGenerator.printCacheStats();
    }
}