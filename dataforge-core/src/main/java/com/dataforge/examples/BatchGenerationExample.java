package com.dataforge.examples;

import com.dataforge.core.BatchGenerationContext;
import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.generators.basic.NameGenerator;

import java.util.List;

/**
 * Example demonstrating batch data generation.
 */
public class BatchGenerationExample {
    
    public static void main(String[] args) {
        // Create a name generator
        DataGenerator<String> nameGenerator = new NameGenerator();
        
        // Create a batch generation context for 1000 records with batch size of 100
        BatchGenerationContext batchContext = new BatchGenerationContext(100, 1000);
        
        System.out.println("Generating names in batches:");
        int batchNumber = 1;
        
        // Generate data in batches
        while (batchContext.hasNext()) {
            List<String> batch = batchContext.generateBatch(nameGenerator);
            System.out.println("Batch " + batchNumber + ": Generated " + batch.size() + " names");
            batchNumber++;
        }
        
        System.out.println("Finished generating all names in batches.");
    }
}