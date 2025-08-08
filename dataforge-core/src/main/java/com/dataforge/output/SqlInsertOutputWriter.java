package com.dataforge.output;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL INSERT语句输出器
 * 将生成的数据输出为SQL INSERT语句
 */
public class SqlInsertOutputWriter {
    
    private final String filePath;
    private final List<String> fieldNames;
    private final Map<String, DataGenerator<?>> generators;
    private final String tableName;
    private final int batchSize;
    private final String databaseType;
    
    public SqlInsertOutputWriter(String filePath, List<String> fieldNames, Map<String, DataGenerator<?>> generators, String tableName) {
        this(filePath, fieldNames, generators, tableName, 100, "mysql");
    }
    
    public SqlInsertOutputWriter(String filePath, List<String> fieldNames, Map<String, DataGenerator<?>> generators, 
                                String tableName, int batchSize, String databaseType) {
        this.filePath = filePath;
        this.fieldNames = fieldNames;
        this.generators = generators;
        this.tableName = tableName;
        this.batchSize = batchSize;
        this.databaseType = databaseType.toLowerCase();
    }
    
    /**
     * 生成数据并写入SQL文件
     */
    public void write(GenerationContext context) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writeHeader(writer, context);
            
            int totalRecords = context.getCount();
            int batchCount = (totalRecords + batchSize - 1) / batchSize;
            
            for (int batch = 0; batch < batchCount; batch++) {
                int startRecord = batch * batchSize;
                int endRecord = Math.min(startRecord + batchSize, totalRecords);
                int currentBatchSize = endRecord - startRecord;
                
                writeBatch(writer, context, startRecord, currentBatchSize);
                writer.write("\n");
            }
            
            writeFooter(writer);
        }
    }
    
    private void writeHeader(FileWriter writer, GenerationContext context) throws IOException {
        writer.write("-- DataForge Generated SQL INSERT Statements\n");
        writer.write("-- Generated at: " + java.time.LocalDateTime.now() + "\n");
        writer.write("-- Total records: " + context.getCount() + "\n");
        writer.write("-- Table: " + tableName + "\n");
        writer.write("-- Database: " + databaseType + "\n");
        writer.write("\n");
        
        // Database-specific settings
        switch (databaseType) {
            case "mysql":
                writer.write("SET FOREIGN_KEY_CHECKS = 0;\n");
                writer.write("SET AUTOCOMMIT = 0;\n");
                break;
            case "postgresql":
                writer.write("BEGIN;\n");
                break;
            case "oracle":
                writer.write("SET DEFINE OFF;\n");
                break;
            case "sqlserver":
                writer.write("SET IDENTITY_INSERT " + tableName + " OFF;\n");
                break;
        }
        writer.write("\n");
    }
    
    private void writeBatch(FileWriter writer, GenerationContext context, int startRecord, int batchSize) throws IOException {
        // Start INSERT statement
        writer.write("INSERT INTO " + getQuotedTableName() + " (");
        
        // Write column names
        String columns = fieldNames.stream()
                .map(this::getQuotedColumnName)
                .collect(Collectors.joining(", "));
        writer.write(columns);
        writer.write(") VALUES\n");
        
        // Generate and write values
        for (int i = 0; i < batchSize; i++) {
            int recordIndex = startRecord + i;
            
            // Create record context
            GenerationContext recordContext = new GenerationContext(1);
            if (context.getSeed() != null) {
                recordContext.setSeed(context.getSeed() + recordIndex);
            }
            
            writer.write("  (");
            
            // Generate field values
            for (int j = 0; j < fieldNames.size(); j++) {
                String fieldName = fieldNames.get(j);
                DataGenerator<?> generator = generators.get(fieldName);
                Object value = generator.generate(recordContext);
                
                writer.write(formatSqlValue(value));
                
                if (j < fieldNames.size() - 1) {
                    writer.write(", ");
                }
            }
            
            writer.write(")");
            
            if (i < batchSize - 1) {
                writer.write(",\n");
            } else {
                writer.write(";\n");
            }
        }
    }
    
    private void writeFooter(FileWriter writer) throws IOException {
        writer.write("\n");
        
        // Database-specific cleanup
        switch (databaseType) {
            case "mysql":
                writer.write("COMMIT;\n");
                writer.write("SET FOREIGN_KEY_CHECKS = 1;\n");
                writer.write("SET AUTOCOMMIT = 1;\n");
                break;
            case "postgresql":
                writer.write("COMMIT;\n");
                break;
            case "oracle":
                writer.write("COMMIT;\n");
                writer.write("SET DEFINE ON;\n");
                break;
            case "sqlserver":
                writer.write("SET IDENTITY_INSERT " + tableName + " ON;\n");
                break;
        }
        
        writer.write("\n-- End of generated SQL\n");
    }
    
    private String getQuotedTableName() {
        switch (databaseType) {
            case "mysql":
                return "`" + tableName + "`";
            case "postgresql":
                return "\"" + tableName + "\"";
            case "oracle":
                return "\"" + tableName.toUpperCase() + "\"";
            case "sqlserver":
                return "[" + tableName + "]";
            default:
                return tableName;
        }
    }
    
    private String getQuotedColumnName(String columnName) {
        switch (databaseType) {
            case "mysql":
                return "`" + columnName + "`";
            case "postgresql":
                return "\"" + columnName + "\"";
            case "oracle":
                return "\"" + columnName.toUpperCase() + "\"";
            case "sqlserver":
                return "[" + columnName + "]";
            default:
                return columnName;
        }
    }
    
    private String formatSqlValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        
        String valueStr = value.toString();
        
        // Check if it's a numeric value (integer or decimal)
        if (isNumeric(valueStr)) {
            return valueStr;
        }
        
        // Check if it's a boolean
        if ("true".equalsIgnoreCase(valueStr) || "false".equalsIgnoreCase(valueStr)) {
            switch (databaseType) {
                case "mysql":
                    return "true".equalsIgnoreCase(valueStr) ? "1" : "0";
                case "postgresql":
                    return valueStr.toUpperCase();
                case "oracle":
                case "sqlserver":
                    return "true".equalsIgnoreCase(valueStr) ? "1" : "0";
                default:
                    return valueStr;
            }
        }
        
        // Treat as string - escape and quote
        return "'" + escapeSqlString(valueStr) + "'";
    }
    
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private String escapeSqlString(String value) {
        if (value == null) {
            return "";
        }
        
        // Escape single quotes by doubling them
        return value.replace("'", "''")
                   .replace("\\", "\\\\")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}