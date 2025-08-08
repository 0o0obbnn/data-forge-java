package com.dataforge.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple LRU (Least Recently Used) cache implementation for data generation.
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 */
public class DataGenerationCache<K, V> {
    
    private final int capacity;
    private final LinkedHashMap<K, V> cache;
    
    public DataGenerationCache(int capacity) {
        this.capacity = capacity;
        // Use access order for LRU behavior
        this.cache = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > DataGenerationCache.this.capacity;
            }
        };
    }
    
    /**
     * Get a value from the cache.
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    public V get(K key) {
        return cache.get(key);
    }
    
    /**
     * Put a value into the cache.
     * 
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void put(K key, V value) {
        cache.put(key, value);
    }
    
    /**
     * Check if the cache contains a key.
     * 
     * @param key the key whose presence in this cache is to be tested
     * @return true if this cache contains a mapping for the specified key
     */
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }
    
    /**
     * Get the current size of the cache.
     * 
     * @return the number of key-value mappings in this cache
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Clear the cache.
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Get the capacity of the cache.
     * 
     * @return the maximum number of entries this cache can hold
     */
    public int getCapacity() {
        return capacity;
    }
}