package com.dataforge.validation;

/**
 * 验证违规项
 */
public class ValidationViolation {
    
    private final String fieldName;
    private final Object value;
    private final String ruleName;
    private final String message;
    
    public ValidationViolation(String fieldName, Object value, String ruleName, String message) {
        this.fieldName = fieldName;
        this.value = value;
        this.ruleName = ruleName;
        this.message = message;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s (值: %s)", 
            fieldName, ruleName, message, value);
    }
}