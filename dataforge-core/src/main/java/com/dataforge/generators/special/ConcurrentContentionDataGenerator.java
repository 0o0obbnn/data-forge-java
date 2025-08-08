package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Concurrent/contention data generator for simulating concurrent access scenarios.
 * Useful for testing thread safety and concurrency handling in applications.
 */
public class ConcurrentContentionDataGenerator implements DataGenerator<String> {
    
    private final Random random;
    
    /**
     * Creates a concurrent/contention data generator with default settings.
     */
    public ConcurrentContentionDataGenerator() {
        this.random = new Random();
    }
    
    @Override
    public String generate(GenerationContext context) {
        // Get parameters
        Object contentionLevelObj = context.getParameter("contention_level", "medium");
        String contentionLevel = contentionLevelObj.toString().toLowerCase();
        
        Object dataTypeObj = context.getParameter("data_type", "timestamp");
        String dataType = dataTypeObj.toString().toLowerCase();
        
        Object threadIdObj = context.getParameter("thread_id", Thread.currentThread().getId());
        long threadId = Long.parseLong(threadIdObj.toString());
        
        // Generate base value based on data type
        String baseValue = generateBaseValue(context, dataType);
        
        // Add contention indicators based on level
        switch (contentionLevel) {
            case "high":
                // High contention: Add timestamp and thread ID for maximum uniqueness
                return baseValue + "_" + System.nanoTime() + "_" + threadId;
            case "low":
                // Low contention: Just add a small random number
                return baseValue + "_" + random.nextInt(100);
            case "medium":
            default:
                // Medium contention: Add timestamp
                return baseValue + "_" + System.currentTimeMillis();
        }
    }
    
    /**
     * Generates a base value of the specified data type.
     * 
     * @param context the generation context
     * @param dataType the type of data to generate
     * @return the generated base value
     */
    private String generateBaseValue(GenerationContext context, String dataType) {
        switch (dataType) {
            case "uuid":
                return java.util.UUID.randomUUID().toString();
            case "name":
                // Generate a random name
                String[] firstNames = {"Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George", "Helen"};
                String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
                return firstNames[random.nextInt(firstNames.length)] + " " + 
                       lastNames[random.nextInt(lastNames.length)];
            case "email":
                // Generate a random email-like string
                String[] domains = {"example.com", "test.org", "demo.net"};
                return "user" + random.nextInt(10000) + "@" + domains[random.nextInt(domains.length)];
            case "integer":
                return String.valueOf(ThreadLocalRandom.current().nextInt(1000000));
            case "timestamp":
            default:
                return String.valueOf(System.currentTimeMillis());
        }
    }
    
    @Override
    public String getName() {
        return "concurrent_contention_data";
    }
    
    @Override
    public java.util.List<String> getSupportedParameters() {
        return java.util.Arrays.asList("contention_level", "data_type", "thread_id");
    }
}