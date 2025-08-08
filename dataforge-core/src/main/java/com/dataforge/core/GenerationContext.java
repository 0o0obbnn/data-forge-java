package com.dataforge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Generation context that holds shared state during data generation.
 */
public class GenerationContext {
    
    private final Random random;
    private final int count;
    private final Map<String, Object> parameters;
    private Long seed;
    
    public GenerationContext(int count) {
        this.count = count;
        this.random = new Random();
        this.parameters = new HashMap<>();
    }
    
    public GenerationContext(int count, long seed) {
        this.count = count;
        this.seed = seed;
        this.random = new Random(seed);
        this.parameters = new HashMap<>();
    }
    
    public Random getRandom() {
        return random;
    }
    
    public int getCount() {
        return count;
    }
    
    /**
     * Get a parameter value with a default value.
     * 
     * @param key the parameter key
     * @param defaultValue the default value
     * @return the parameter value or default value
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, T defaultValue) {
        Object value = parameters.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }
    
    /**
     * Set a parameter value.
     * 
     * @param key the parameter key
     * @param value the parameter value
     */
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }
    
    /**
     * Get all parameters.
     * 
     * @return the parameters map
     */
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }
    
    /**
     * Get the seed value used for random generation.
     * 
     * @return the seed value
     */
    public Long getSeed() {
        return seed;
    }
    
    /**
     * Set the seed value for random generation.
     * Note: This creates a new Random instance with the specified seed.
     * 
     * @param seed the seed value
     */
    public void setSeed(Long seed) {
        this.seed = seed;
        if (seed != null) {
            // Replace the random instance with a seeded one
            try {
                java.lang.reflect.Field randomField = this.getClass().getDeclaredField("random");
                randomField.setAccessible(true);
                randomField.set(this, new Random(seed));
            } catch (Exception e) {
                // Fallback: set the seed on the existing random instance
                this.random.setSeed(seed);
            }
        }
    }
}