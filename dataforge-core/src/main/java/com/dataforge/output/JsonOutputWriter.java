package com.dataforge.output;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON output writer for writing generated data to a JSON file.
 */
public class JsonOutputWriter {
    
    private final String filePath;
    private final List<String> fieldNames;
    private final Map<String, DataGenerator<?>> generators;
    
    public JsonOutputWriter(String filePath, List<String> fieldNames, Map<String, DataGenerator<?>> generators) {
        this.filePath = filePath;
        this.fieldNames = fieldNames;
        this.generators = generators;
    }
    
    public void write(GenerationContext context) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Start JSON array
            writer.write("[\n");
            
            // Generate data records
            for (int i = 0; i < context.getCount(); i++) {
                GenerationContext rowContext = new GenerationContext(1);
                
                // Start JSON object for this record
                writer.write("  {\n");
                
                // Add fields to the JSON object
                for (int j = 0; j < fieldNames.size(); j++) {
                    String fieldName = fieldNames.get(j);
                    DataGenerator<?> generator = generators.get(fieldName);
                    
                    // Add field name
                    writer.write("    \"");
                    writer.write(escapeJsonString(fieldName));
                    writer.write("\": ");
                    
                    if (generator != null) {
                        Object value = generator.generate(rowContext);
                        if (value != null) {
                            // Add value based on its type
                            if (value instanceof String) {
                                writer.write("\"");
                                writer.write(escapeJsonString((String) value));
                                writer.write("\"");
                            } else if (value instanceof Integer || value instanceof Long || 
                                       value instanceof Double || value instanceof Boolean) {
                                writer.write(value.toString());
                            } else {
                                // Default to string representation
                                writer.write("\"");
                                writer.write(escapeJsonString(value.toString()));
                                writer.write("\"");
                            }
                        } else {
                            writer.write("null");
                        }
                    } else {
                        writer.write("null");
                    }
                    
                    // Add comma if not the last field
                    if (j < fieldNames.size() - 1) {
                        writer.write(",");
                    }
                    
                    writer.write("\n");
                }
                
                // End JSON object for this record
                writer.write("  }");
                
                // Add comma if not the last record
                if (i < context.getCount() - 1) {
                    writer.write(",");
                }
                
                writer.write("\n");
            }
            
            // End JSON array
            writer.write("]\n");
        }
    }
    
    /**
     * Escapes special characters in a string for JSON output.
     */
    private String escapeJsonString(String str) {
        if (str == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        // Escape control characters
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}