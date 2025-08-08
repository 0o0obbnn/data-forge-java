package com.dataforge.output;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CSV output writer for writing generated data to a CSV file.
 */
public class CsvOutputWriter {
    
    private final String filePath;
    private final List<String> fieldNames;
    private final Map<String, DataGenerator<?>> generators;
    
    public CsvOutputWriter(String filePath, List<String> fieldNames, Map<String, DataGenerator<?>> generators) {
        this.filePath = filePath;
        this.fieldNames = new ArrayList<>(fieldNames);
        this.generators = generators;
    }
    
    public void write(GenerationContext context) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println(String.join(",", fieldNames));
            
            // Generate and write data rows
            for (int i = 0; i < context.getCount(); i++) {
                GenerationContext rowContext = new GenerationContext(1);
                List<String> values = new ArrayList<>();
                
                for (String fieldName : fieldNames) {
                    DataGenerator<?> generator = generators.get(fieldName);
                    if (generator != null) {
                        Object value = generator.generate(rowContext);
                        values.add(value != null ? value.toString() : "");
                    } else {
                        values.add("");
                    }
                }
                
                writer.println(String.join(",", values));
            }
        }
    }
}