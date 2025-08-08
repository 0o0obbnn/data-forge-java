package com.dataforge.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Batch generation context that holds shared state during batch data generation.
 */
public class BatchGenerationContext {
    
    private final Random random;
    private final int batchSize;
    private final int totalSize;
    private int currentIndex;
    
    public BatchGenerationContext(int batchSize, int totalSize) {
        this.batchSize = batchSize;
        this.totalSize = totalSize;
        this.currentIndex = 0;
        this.random = new Random();
    }
    
    public Random getRandom() {
        return random;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public int getTotalSize() {
        return totalSize;
    }
    
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    public void incrementIndex() {
        currentIndex++;
    }
    
    public boolean hasNext() {
        return currentIndex < totalSize;
    }
    
    /**
     * Generate a batch of data items
     * 
     * @param generator the data generator to use
     * @param <T> the type of data to generate
     * @return a list of generated data items
     */
    public <T> List<T> generateBatch(DataGenerator<T> generator) {
        List<T> batch = new ArrayList<>(batchSize);
        int count = 0;
        
        while (count < batchSize && hasNext()) {
            batch.add(generator.generate(new GenerationContext(1)));
            incrementIndex();
            count++;
        }
        
        return batch;
    }
}